package com.urd.triple;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Player;
import com.urd.triple.widget.SelfWidget;

public class GameActivity extends BaseActivity {
    private SelfWidget mSelfWidget;

    public static void launch(Context context, Intent intent) {
        intent.setClass(context, GameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);
        GameCore.getInstance().registerListener(mGameListener);

        setupViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GameCore.getInstance().unregisterListener(mGameListener);
    }

    private void setupViews() {
        mSelfWidget = (SelfWidget) findViewById(R.id.self);
        mSelfWidget.setPlayer(GameCore.getInstance().getSelf());
    }

    private final GameListener mGameListener = new GameListener() {

        @Override
        public void onPlayerLogout(Player player) {
        }

        @Override
        public void onPlayerLogin(Player player) {
        }

        @Override
        public void onLoginSuccess(Collection<Player> players) {
        }

        @Override
        public void onLoginFailed(int errorCode) {
        }

        @Override
        public void onGameStart(int role, Player lord) {
        }

        @Override
        public void onHeroList(List<Integer> heroes) {
        }

        @Override
        public void onPlayerHeroSelected(Player player, int hero) {
        }

        @Override
        public void onCardAction(Card card, int srcArea, int dstArea, Player src, Player dst) {
        }

        @Override
        public void onDeskClean() {
        }

        @Override
        public void onPlayerHPChanged(Player player) {
            if (player == GameCore.getInstance().getSelf()) {
                mSelfWidget.updateHp();
            } else {
                // TODO 其他人掉血的逻辑
            }
        }

        @Override
        public void onPlayerRole(Player player) {
            if (player == GameCore.getInstance().getSelf()) {
                mSelfWidget.updateRole();
            }
        }

        @Override
        public void onNetworkError() {
        }
    };

}
