package com.urd.triple;

import java.util.Collection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

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
        GameCore.getInstance().close();
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
        public void onNetworkError() {

        }

        @Override
        public void onLoginSuccess(Collection<Player> players) {

        }

        @Override
        public void onLoginFailed(int errorCode) {

        }
    };

    private static final OnClickListener mGetCardClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

        }
    };

}
