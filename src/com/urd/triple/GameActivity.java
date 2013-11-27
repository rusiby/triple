package com.urd.triple;

import java.util.Collection;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Player;
import com.urd.triple.widget.SelectHeroView;
import com.urd.triple.widget.SelfWidget;

public class GameActivity extends BaseActivity {
    private Dialog mSelecHeroDialog;
    private SelectHeroView mSelcetHeroContaner;
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

        showSlectHeroDialog();

    }

    private void showSlectHeroDialog() {

        mSelecHeroDialog = new Dialog(this,
                android.R.style.Theme_Light_Panel);
        shadow(mSelecHeroDialog);
        mSelcetHeroContaner = new SelectHeroView(this);
        mSelcetHeroContaner.setHeros(GameCore.getInstance().getSelf().heroes);
        mSelecHeroDialog.getWindow().setContentView(
                mSelcetHeroContaner);
        if (GameCore.getInstance().getSelf().heroes != null && GameCore.getInstance().getSelf().heroes.size() > 0) {
            mSelecHeroDialog.show();
        }
        mSelecHeroDialog.setCancelable(false);
        mSelecHeroDialog.setCanceledOnTouchOutside(false);

    }

    /**
     * 设置阴影
     * 
     * @param dialog
     */
    private void shadow(Dialog dialog) {
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lp.dimAmount = 0.3f;
        dialog.getWindow().setAttributes(lp);
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
            if (!mSelecHeroDialog.isShowing()) {
                mSelecHeroDialog.show();
                mSelcetHeroContaner.setHeros(heroes);
            }
        }

        @Override
        public void onPlayerHeroSelected(Player player, int hero) {
            if (getDefaultSharedPreferences().getString("nickName", "").trim().equals(player.name)) {
                showToast("您选择的英雄是:" + hero);
            } else {
                showToast(player.name + "选择的英雄是:" + hero);
            }
            if (mSelecHeroDialog.isShowing()) {
                mSelecHeroDialog.dismiss();
            }
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
