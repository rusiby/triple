package com.urd.triple.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.urd.triple.core.GameSocket.GameSocketListener;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.LogoutNotify;

public class GameRouter implements GameSocketListener {
    private static final Logger LOG = LoggerFactory.getLogger(GameRouter.class);

    private final UUID mUUID;
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private BluetoothGameSocket mWanSocket;
    private List<GameSocket> mClients;
    private AcceptThread mAcceptThread;

    public GameRouter(UUID uuid) {
        LOG.debug("construction. UUID={}", uuid);

        mUUID = uuid;
        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mWanSocket = new BluetoothGameSocket(mWanSocketListener, uuid);
        mClients = new ArrayList<GameSocket>();
    }

    public void start(BluetoothDevice device) {
        LOG.info("start.");

        // WAN
        mWanSocket.connect(device);

        // LAN
        mAcceptThread = new AcceptThread();
        mAcceptThread.start();
    }

    public void stop() {
        LOG.info("stop.");

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        for (GameSocket socket : mClients) {
            socket.close();
        }
        mClients.clear();
        mWanSocket.close();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void connectLocalSocket(final LocalGameSocket socket) {
        LOG.info("connect local socket. name={}", socket.getName());

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mAcceptThread.isAlive()) {
                    LocalGameSocket s = new LocalGameSocket(GameRouter.this, socket);
                    LOG.info("local socket accept.");
                    mClients.add(s);
                } else {
                    socket.close();
                }
            }
        });
    }

    public List<GameSocket> getClients() {
        return mClients;
    }

    @Override
    public void onSocketConnect(GameSocket socket) {
    }

    @Override
    public void onSocketCommand(GameSocket socket, Command command) {
        LOG.debug("recv command. from={} command={}", socket.getName(), command);

        command.src = socket.getID();
        routeTo(command);
    }

    @Override
    public void onSocketError(GameSocket socket) {
        LOG.warn("{} socket error. remove it.", socket.getName());

        mClients.remove(socket);

        routeTo(new LogoutNotify(socket.getID()));
        socket.close();
    }

    private void routeTo(Command command) {
        mWanSocket.send(command);
    }

    private void onSocketAccepted(final BluetoothSocket socket) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                BluetoothGameSocket gameSocket = new BluetoothGameSocket(GameRouter.this, socket);
                LOG.info("socket accept. name={}({})", gameSocket.getName());
                mClients.add(gameSocket);
            }
        });
    }

    private final GameSocketListener mWanSocketListener = new GameSocketListener() {

        @Override
        public void onSocketConnect(GameSocket socket) {
            LOG.info("WAN socket connected.");
        }

        @Override
        public void onSocketCommand(GameSocket socket, Command command) {
            LOG.debug("WAN recv a command. command={}", command);

            for (GameSocket s : mClients) {
                if (s.getID().equals(command.dst__)) {
                    LOG.error("{}", s);
                    s.send(command);
                    break;
                }
            }
        }

        @Override
        public void onSocketError(GameSocket socket) {
            LOG.warn("WAN socket error. stop now.");

            stop();
        }
    };

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("GameServer", mUUID);
            } catch (IOException e) {
                LOG.warn("listen failed. what={}", e.getMessage());
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (mmServerSocket != null) {
                LOG.info("accpet begin.");

                BluetoothSocket socket = null;
                while (true) {
                    try {
                        socket = mmServerSocket.accept();
                    } catch (IOException e) {
                        LOG.warn("accpet failed. what={}", e.getMessage());
                        break;
                    }
                    onSocketAccepted(socket);
                }

                LOG.info("accpet end.");
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            LOG.debug("cancel.");

            if (mmServerSocket != null) {
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    LOG.warn("close failed.", e);
                }
            }
        }
    }
}
