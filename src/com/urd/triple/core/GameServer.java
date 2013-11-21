package com.urd.triple.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.urd.triple.core.GameSocket.GameSocketListener;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.LoginNotify;
import com.urd.triple.core.commands.LoginReq;
import com.urd.triple.core.commands.LoginResp;
import com.urd.triple.core.commands.LogoutNotify;

public class GameServer implements GameSocketListener {
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

    private final UUID mUUID;
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private List<GameSocket> mClients;
    private PlayerMananger mPlayerMananger;
    private AcceptThread mAcceptThread;

    public GameServer(UUID uuid) {
        LOG.debug("construction. UUID={}", uuid);

        mUUID = uuid;
        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mClients = new ArrayList<GameSocket>();
        mPlayerMananger = new PlayerMananger();
    }

    public void start() {
        LOG.info("start.");

        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    public void stop() {
        LOG.info("stop.");

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public void connectLocalSocket(final LocalGameSocket socket) {
        LOG.info("connect local socket. name={}", socket.getName());

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                LocalGameSocket s = new LocalGameSocket(GameServer.this, socket);
                LOG.info("local socket accept.");
                mClients.add(s);
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

        String id = socket.getID();
        switch (command.getID()) {
        case LoginReq.ID:
            if (!(mPlayerMananger.contains(id))) {
                Player player = mPlayerMananger.add(socket, ((LoginReq) command).name);
                LOG.info("playe login. player={}", player);

                sendTo(new LoginResp(0, mPlayerMananger.getPlayers()), player);
                broadcastExcept(new LoginNotify(player), player);
            } else {
                LOG.warn("invalid command. close socket.");

                socket.close();
            }
            break;

        default:
            command.src = id;
            broadcast(command);
            break;
        }
    }

    @Override
    public void onSocketError(GameSocket socket) {
        LOG.warn("{} socket error. remove it.", socket.getName());

        mClients.remove(socket);
        Player player = mPlayerMananger.remove(socket);
        if (player != null) {
            broadcast(new LogoutNotify(socket.getID()));
        }
        socket.close();
    }

    private void sendTo(Command command, Player player) {
        LOG.debug("send command to {}. command={}", player.name, command);

        player.socket.send(command);
    }

    private void broadcast(Command command) {
        LOG.debug("broadcast command. command={}", command);

        for (Player p : mPlayerMananger.getPlayers()) {
            p.socket.send(command);
        }
    }

    private void broadcastExcept(Command command, Player player) {
        LOG.debug("broadcast command except {}. command={}", player.name, command);

        for (Player p : mPlayerMananger.getPlayers()) {
            if (p != player) {
                p.socket.send(command);
            }
        }
    }

    private void onSocketAccepted(final BluetoothSocket socket) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                BluetoothGameSocket gameSocket = new BluetoothGameSocket(GameServer.this, socket);
                LOG.info("socket accept. name={}({})", gameSocket.getName());
                mClients.add(gameSocket);
            }
        });
    }

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
