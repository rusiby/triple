package com.urd.triple.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import com.urd.triple.core.GameSocket.GameSocketListener;
import com.urd.triple.core.commands.CardActionNotify;
import com.urd.triple.core.commands.CardActionReq;
import com.urd.triple.core.commands.ChangeHPNotify;
import com.urd.triple.core.commands.CleanDeskNotify;
import com.urd.triple.core.commands.CleanDeskReq;
import com.urd.triple.core.commands.Command;
import com.urd.triple.core.commands.HeroListNotify;
import com.urd.triple.core.commands.LoginNotify;
import com.urd.triple.core.commands.LoginReq;
import com.urd.triple.core.commands.LoginResp;
import com.urd.triple.core.commands.LogoutNotify;
import com.urd.triple.core.commands.RoleNotify;
import com.urd.triple.core.commands.SelectHeroNotify;
import com.urd.triple.core.commands.StartGameNotify;
import com.urd.triple.core.commands.StartGameReq;
import com.urd.triple.core.commands.StartGameResp;

public class GameServer implements GameSocketListener {
    private static final Logger LOG = LoggerFactory.getLogger(GameServer.class);

    private final UUID mUUID;
    private final Handler mHandler;
    private final BluetoothAdapter mBluetoothAdapter;
    private List<GameSocket> mClients;
    private AcceptThread mAcceptThread;

    private PlayerMananger mPlayerMananger;
    private boolean mPlaying;
    private Deck mDeck;
    private GameProxy mGameProxy;

    public GameServer(UUID uuid) {
        LOG.debug("construction. UUID={}", uuid);

        mUUID = uuid;
        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mClients = new ArrayList<GameSocket>();
        mPlayerMananger = new PlayerMananger();
        mPlaying = false;
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
        for (GameSocket socket : mClients) {
            socket.close();
        }
        mClients.clear();
        mPlayerMananger.clear();
        mPlaying = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    public void connectLocalSocket(final LocalGameSocket socket) {
        LOG.info("connect local socket. name={}", socket.getName());

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mAcceptThread.isAlive()) {
                    LocalGameSocket s = new LocalGameSocket(GameServer.this, socket);
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

    // TODO: 命令状态认证
    @Override
    public void onSocketCommand(GameSocket socket, Command command) {
        LOG.debug("recv command. from={} command={}", socket.getName(), command);

        // 当命令经过路由时 src != null
        if (command.src == null) {
            command.src = socket.getID();
        }
        switch (command.getID()) {
        case LoginReq.ID:
            if (!mPlaying && !(mPlayerMananger.contains(command.src))) {
                Player player = mPlayerMananger.add(socket, command.src, ((LoginReq) command).name);
                LOG.info("playe login. player={}", player);

                sendTo(new LoginResp(0, new ArrayList<Player>(mPlayerMananger.getPlayers())), player);
                broadcastExcept(new LoginNotify(player), player);
            } else {
                LOG.warn("invalid command. close socket. command={}", command);
            }
            break;

        case LogoutNotify.ID:
            onLogoutNotify((LogoutNotify) command);
            break;

        case StartGameReq.ID:
            if (!mPlaying) {
                onStartGameReq(mPlayerMananger.get(command.src));
            } else {
                LOG.warn("invalid command. command={}", command);
            }
            break;

        case SelectHeroNotify.ID:
            onSelectHeroNotify(mPlayerMananger.get(command.src), (SelectHeroNotify) command);
            break;

        case CardActionReq.ID:
            onCardActionReq(mPlayerMananger.get(command.src), (CardActionReq) command);
            break;

        case CleanDeskReq.ID:
            onCleanDeskReq(mPlayerMananger.get(command.src));
            break;

        case ChangeHPNotify.ID:
            onChangeHPNotify(mPlayerMananger.get(command.src), (ChangeHPNotify) command);
            break;

        case RoleNotify.ID:
            onRoleNotify(mPlayerMananger.get(command.src), (RoleNotify) command);
            break;

        default:
            LOG.warn("invalid command. command={}", command);
            break;
        }
    }

    @Override
    public void onSocketError(GameSocket socket) {
        LOG.warn("{} socket error. remove it.", socket.getName());

        mClients.remove(socket);
        Collection<Player> players = mPlayerMananger.remove(socket);
        for (Player player : players) {
            broadcast(new LogoutNotify(player.id));
        }
        socket.close();
    }

    private void onLogoutNotify(LogoutNotify notify) {
        Player player = mPlayerMananger.remove(notify.id);
        if (player != null) {
            LOG.info("{} logout.", player.name);

            broadcast(notify);
        }
    }

    private void onStartGameReq(Player player) {
        int playerCount = mPlayerMananger.size();

        LOG.info("request start game. players.count={}", playerCount);

        if (playerCount >= GameConstants.MIN_PLAYER_COUNT) {
            LOG.info("game start.");

            String lordID = null;
            Role.Generator generator = new Role.Generator(playerCount);
            List<Player> players = mPlayerMananger.getPlayers();
            for (Player p : players) {
                p.role = generator.generate();
                if (p.role == Role.LORD) {
                    lordID = p.id;
                }
            }
            for (Player p : players) {
                sendTo(new StartGameNotify(p.role, lordID), p);

                Hero.Generator heroGenerator = new Hero.Generator();
                if (p.role == Role.LORD) {
                    sendTo(new HeroListNotify(heroGenerator.generate(5)), p);
                }
            }

            mDeck = new Deck();
            mGameProxy = new GameProxy(mPlayerMananger);
            mPlaying = true;
        } else {
            LOG.warn("invalid player count.");

            sendTo(new StartGameResp(StartGameResp.ERROR_INVALID_PLAYER_COUNT), player);
        }
    }

    private void onSelectHeroNotify(Player player, SelectHeroNotify notify) {
        LOG.info("{} select hero {}.", player.name, Hero.valueOf(notify.hero).name);

        broadcast(notify);

        if (player.isLord()) {
            Hero.Generator generator = new Hero.Generator(player.hero);
            for (Player p : mPlayerMananger.getPlayers()) {
                if (p != player) {
                    sendTo(new HeroListNotify(generator.generate(3)), p);
                }
            }
        }
    }

    private void onCardActionReq(Player player, CardActionReq req) {
        LOG.info("card action req. req={}", req);

        CardActionNotify notify = new CardActionNotify(req);
        if (req.action.srcArea == Card.AREA_DECK) {
            Card card = mDeck.pull();
            notify.action.card = card.id;
        }
        if (req.action.dstArea == Card.AREA_DECK_TOP || req.action.dstArea == Card.AREA_DECK_BOTTOM) {
            mDeck.add(new Card(req.action.card), notify.action.dstArea);
        }

        broadcast(notify);
    }

    private void onCleanDeskReq(Player player) {
        LOG.info("clean desk req.");

        mDeck.push(mGameProxy.getDeskCards());

        broadcast(new CleanDeskNotify());
    }

    private void onChangeHPNotify(Player player, ChangeHPNotify notify) {
        LOG.info("change hp notify.");

        broadcast(notify);
    }

    private void onRoleNotify(Player player, RoleNotify notify) {
        LOG.info("role notify.");

        broadcast(notify);
    }

    private void sendTo(Command command, Player player) {
        LOG.debug("send command to {}. command={}", player.name, command);

        internalSendTo(player, command);

        if (mGameProxy != null) {
            mGameProxy.execute(command);
        }
    }

    private void broadcast(Command command) {
        LOG.debug("broadcast command. command={}", command);

        for (Player p : mPlayerMananger.getPlayers()) {
            try {
                internalSendTo(p, command.clone());
            } catch (CloneNotSupportedException e) {
                LOG.warn("clone failed.", e);
            }
        }

        if (mGameProxy != null) {
            mGameProxy.execute(command);
        }
    }

    private void broadcastExcept(Command command, Player player) {
        LOG.debug("broadcast command except {}. command={}", player.name, command);

        for (Player p : mPlayerMananger.getPlayers()) {
            if (p != player) {
                try {
                    internalSendTo(p, command.clone());
                } catch (CloneNotSupportedException e) {
                    LOG.warn("clone failed.", e);
                }
            }
        }

        if (mGameProxy != null) {
            mGameProxy.execute(command);
        }
    }

    private void internalSendTo(Player player, Command command) {
        command.dst__ = player.id;
        player.socket.send(command);
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
