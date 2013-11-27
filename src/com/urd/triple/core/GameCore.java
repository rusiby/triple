package com.urd.triple.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

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

public class GameCore {
    private static final Logger LOG = LoggerFactory.getLogger(GameCore.class);

    public static GameCore sInstance = null;

    private Player mSelf;
    private PlayerMananger mPlayerMananger;
    private GameProxy mGameProxy;
    private GameServer mServer;
    private GameSocket mClient;
    private Set<GameListener> mListeners;

    public interface GameListener {

        void onLoginSuccess(Collection<Player> players);

        void onLoginFailed(int errorCode);

        void onPlayerLogin(Player player);

        void onPlayerLogout(Player player);

        void onGameStart(int role, Player lord);

        void onHeroList(List<Integer> heroes);

        void onPlayerHeroSelected(Player player, int hero);

        void onCardAction(Card card, int srcArea, int dstArea, Player src, Player dst);

        void onDeskClean();

        void onPlayerHPChanged(Player player);

        void onPlayerRole(Player player);

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

    public void startGame() {
        LOG.info("start game.");

        if (mPlayerMananger.size() >= GameConstants.MIN_PLAYER_COUNT) {
            mClient.send(new StartGameReq());
        }
    }

    public void selectHero(int hero) {
        LOG.info("select hero {}.", hero);

        mClient.send(new SelectHeroNotify(hero));
    }

    public void doCardAction(int card, int srcArea, int dstArea, Player target) {
        String id = null;
        if (target != null) {
            id = target.id;
        }
        CardActionReq req = new CardActionReq(card, srcArea, dstArea, id);
        LOG.info("do card aciton. req={}", req);
        mClient.send(req);
    }

    public void cleanDesk() {
        LOG.info("clean desk.");

        mClient.send(new CleanDeskReq());
    }

    public void changeHP(int hp) {
        LOG.info("change hp. {} => {}", mSelf.hp, hp);

        mClient.send(new ChangeHPNotify(hp));
    }

    public void showRole() {
        LOG.info("show role. role={}", mSelf.role);

        mClient.send(new RoleNotify(mSelf.role));
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
        if (mGameProxy != null) {
            mGameProxy.clear();
            mGameProxy = null;
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

    private void onStartGameNotify(StartGameNotify notify) {
        LOG.info("game start. role={}", notify.role);

        mGameProxy = new GameProxy(mPlayerMananger);

        mGameProxy.execute(notify);

        for (GameListener l : mListeners) {
            l.onGameStart(notify.role, mPlayerMananger.get(notify.lordID));
        }
    }

    private void onHeroListNotify(HeroListNotify notify) {
        LOG.info("hero list notify. heroes={}", notify.heroes);

        for (GameListener l : mListeners) {
            l.onHeroList(notify.heroes);
        }
    }

    private void onSelectHeroNotify(SelectHeroNotify notify) {
        Player player = mPlayerMananger.get(notify.src);

        LOG.info("select hero. player={} hero={}", player.name, notify.hero);

        for (GameListener l : mListeners) {
            l.onPlayerHeroSelected(player, notify.hero);
        }
    }

    private void onCardActionNotify(CardActionNotify notify) {
        Player src = mPlayerMananger.get(notify.src);
        Player dst = null;
        if (notify.dst != null) {
            dst = mPlayerMananger.get(notify.dst);
        }
        Card card = mGameProxy.getCard(notify.card, notify.dstArea, src);

        LOG.info("card action. action={}", notify);

        for (GameListener l : mListeners) {
            l.onCardAction(card, notify.srcArea, notify.dstArea, src, dst);
        }
    }

    private void onCleanDeskNotify() {
        LOG.info("desk clean.");

        for (GameListener l : mListeners) {
            l.onDeskClean();
        }
    }

    private void onChangeHPNotify(ChangeHPNotify notify) {
        Player player = mPlayerMananger.get(notify.src);
        LOG.info("hp changed. player={} hp={}", player.name, player.hp);

        for (GameListener l : mListeners) {
            l.onPlayerHPChanged(player);
        }
    }

    private void onRoleNotify(RoleNotify notify) {
        Player player = mPlayerMananger.get(notify.src);
        LOG.info("role notify. player={} hp=role", player.name, player.role);

        for (GameListener l : mListeners) {
            l.onPlayerRole(player);
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

            if (mGameProxy != null) {
                mGameProxy.execute(command);
            }

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

            case StartGameNotify.ID:
                onStartGameNotify((StartGameNotify) command);
                break;

            case HeroListNotify.ID:
                onHeroListNotify((HeroListNotify) command);
                break;

            case SelectHeroNotify.ID:
                onSelectHeroNotify((SelectHeroNotify) command);
                break;

            case CardActionNotify.ID:
                onCardActionNotify((CardActionNotify) command);
                break;

            case CleanDeskNotify.ID:
                onCleanDeskNotify();
                break;

            case ChangeHPNotify.ID:
                onChangeHPNotify((ChangeHPNotify) command);
                break;

            case RoleNotify.ID:
                onRoleNotify((RoleNotify) command);
                break;

            default:
                break;
            }
        }

        @Override
        public void onSocketError(GameSocket socket) {
            close();

            for (GameListener l : mListeners) {
                l.onNetworkError();
            }
        }
    };
}
