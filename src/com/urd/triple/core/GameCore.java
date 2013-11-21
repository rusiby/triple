package com.urd.triple.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.urd.triple.core.GameSocket.GameSocketListener;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.LoginNotify;
import com.urd.triple.core.commands.LoginReq;
import com.urd.triple.core.commands.LoginResp;
import com.urd.triple.core.commands.LogoutNotify;

public class GameCore {
    private static final Logger LOG = LoggerFactory.getLogger(GameCore.class);

    public static GameCore sInstance = null;

    private Player mSelf;
    private PlayerMananger mPlayerMananger;
    private GameServer mServer;
    private GameSocket mClient;
    private Set<GameListener> mListeners;

    public interface GameListener {

        void onLoginSuccess(Collection<Player> players);

        void onLoginFailed(int errorCode);

        void onPlayerLogin(Player player);

        void onPlayerLogout(Player player);

        void onNetworkError();
    }

    public static void init() {
        LOG.info("init.");

        sInstance = new GameCore();
    }

    public static void fini() {
        if (sInstance != null) {
            LOG.info("fini.");

            sInstance.close();
            sInstance = null;
        }
    }

    public static GameCore getInstance() {
        return sInstance;
    }

    public static boolean isValid() {
        return sInstance != null;
    }

    public void setup(String name) {
        mSelf = new Player(BluetoothAdapter.getDefaultAdapter().getAddress(), name);
    }

    public void startGameServer(UUID uuid) {
        if (isIdle()) {
            mServer = new GameServer(uuid);
            mServer.start();

            LocalGameSocket socket = new LocalGameSocket(mSocketListener, null);
            mServer.connectLocalSocket(socket);
            mClient = socket;
        }
    }

    public void connect(UUID uuid, BluetoothDevice device) {
        BluetoothGameSocket socket = new BluetoothGameSocket(mSocketListener, uuid);
        socket.connect(device);
        mClient = socket;
    }

    public void registerListener(GameListener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(GameListener listener) {
        mListeners.remove(listener);
    }

    public void close() {
        LOG.info("close.");

        if (mClient != null) {
            mClient.close();
            mClient = null;
        }
        if (mServer != null) {
            mServer.stop();

            mServer = null;
        }
        mPlayerMananger.clear();
    }

    public Player getSelf() {
        return mSelf;
    }

    public Collection<Player> getPlayers() {
        return mPlayerMananger.getPlayers();
    }

    public boolean isMaster() {
        return mServer != null;
    }

    public boolean isSlave() {
        return mServer == null && mClient != null;
    }

    public boolean isIdle() {
        return mServer == null && mClient == null;
    }

    private GameCore() {
        mPlayerMananger = new PlayerMananger();
        mListeners = new HashSet<GameCore.GameListener>();
    }

    private void onLoginResp(LoginResp resp) {
        if (resp.errorCode == 0) {
            LOG.info("login success. players.size={}", resp.players.size());

            mPlayerMananger.addAll(resp.players);
            mSelf = mPlayerMananger.get(mSelf.id);

            for (GameListener l : mListeners) {
                l.onLoginSuccess(resp.players);
            }
        } else {
            LOG.warn("login failed. err={}", resp.errorCode);

            for (GameListener l : mListeners) {
                l.onLoginFailed(resp.errorCode);
            }

            close();
        }
    }

    private void onPlayerLogin(LoginNotify notify) {
        LOG.info("player {} login.", notify.player.name);

        mPlayerMananger.add(notify.player);

        for (GameListener l : mListeners) {
            l.onPlayerLogin(notify.player);
        }
    }

    private void onPlayerLogout(LogoutNotify notify) {
        Player player = mPlayerMananger.get(notify.id);
        if (player != null) {
            LOG.info("player {} logout.", player.name);

            mPlayerMananger.remove(player);

            for (GameListener l : mListeners) {
                l.onPlayerLogout(player);
            }
        }
    }

    private final GameSocketListener mSocketListener = new GameSocketListener() {

        @Override
        public void onSocketConnect(GameSocket socket) {
            LOG.info("connected. try login.");

            mClient.send(new LoginReq(mSelf.name));
        }

        @Override
        public void onSocketCommand(GameSocket socket, Command command) {
            LOG.debug("recv a command. id={}", command.getID());

            switch (command.getID()) {
            case LoginResp.ID:
                onLoginResp((LoginResp) command);
                break;

            case LoginNotify.ID:
                onPlayerLogin((LoginNotify) command);
                break;

            case LogoutNotify.ID:
                onPlayerLogout((LogoutNotify) command);
                break;

            default:
                break;
            }
        }

        @Override
        public void onSocketError(GameSocket socket) {
            for (GameListener l : mListeners) {
                l.onNetworkError();
            }

            close();
        }
    };
}
