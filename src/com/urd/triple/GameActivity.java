package com.urd.triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Hero;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Player;
import com.urd.triple.widget.OthersWidget;
import com.urd.triple.widget.DeskCardView;
import com.urd.triple.widget.SelectHeroView;
import com.urd.triple.widget.SelfWidget;

public class GameActivity extends BaseActivity {
    private Dialog mSelecHeroDialog;
    private SelectHeroView mSelcetHeroContaner;
    private SelfWidget mSelfWidget;
    private List<OthersWidget> mOthersWidgetList;
    private DeskCardView mDeskCard;

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
        Player self = GameCore.getInstance().getSelf();
        mSelfWidget.setPlayer(self);

        OthersWidget other01 = (OthersWidget) findViewById(R.id.others01);
        OthersWidget other02 = (OthersWidget) findViewById(R.id.others02);
        OthersWidget other03 = (OthersWidget) findViewById(R.id.others03);
        OthersWidget other04 = (OthersWidget) findViewById(R.id.others04);
        OthersWidget other05 = (OthersWidget) findViewById(R.id.others05);
        OthersWidget other06 = (OthersWidget) findViewById(R.id.others06);
        OthersWidget other07 = (OthersWidget) findViewById(R.id.others07);

        mOthersWidgetList = new ArrayList<OthersWidget>();
        mOthersWidgetList.add(other01);
        mOthersWidgetList.add(other02);
        mOthersWidgetList.add(other03);
        mOthersWidgetList.add(other04);
        mOthersWidgetList.add(other05);
        mOthersWidgetList.add(other06);
        mOthersWidgetList.add(other07);

        Collection<Player> players = GameCore.getInstance().getPlayers();
        if (players != null) {
            int size = mOthersWidgetList.size();

            int index = 0;
            Iterator<Player> iter = players.iterator();
            while (iter.hasNext()) {
                Player player = (Player) iter.next();
                if (player != self && index < size) {
                    mOthersWidgetList.get(index++).setPlayer(player);
                }
            }
        }

        showSlectHeroDialog();
    }

    private void updateOthers() {
        Collection<Player> players = GameCore.getInstance().getPlayers();
        if (players != null) {
            int size = mOthersWidgetList.size();

            int index = 0;
            Iterator<Player> iter = players.iterator();
            while (iter.hasNext()) {
                Player player = (Player) iter.next();
                if (player != GameCore.getInstance().getSelf() && index < size) {
                    mOthersWidgetList.get(index++).update();
                }
            }
        }

        mDeskCard = (DeskCardView) findViewById(R.id.desk_card);
    }

    private void showSlectHeroDialog() {

        mSelecHeroDialog = new Dialog(this,
                android.R.style.Theme_Light_Panel);
        shadow(mSelecHeroDialog);
        mSelcetHeroContaner = new SelectHeroView(this);
        mSelcetHeroContaner.setHeros(GameCore.getInstance().getSelf().heroes);
        mSelecHeroDialog.getWindow().setContentView(
                mSelcetHeroContaner);
        mSelecHeroDialog.show();
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
                mSelcetHeroContaner.setHeros(heroes);
                mSelecHeroDialog.show();
            }
        }

        @Override
        public void onPlayerHeroSelected(Player player, int hero) {
            if (getDefaultSharedPreferences().getString("nickName", "").trim().equals(player.name)) {
                showToast("您选择的英雄是:" + Hero.valueOf(hero).name);
            } else {
                // 主公才收到通知
                if (player.isLord()) {
                    showToast(player.name + "选择的英雄是:" + Hero.valueOf(hero).name);
                }
            }
            if (player == GameCore.getInstance().getSelf()) {
                mSelfWidget.updateSkills();
            }

            if (mSelecHeroDialog.isShowing()) {
                mSelecHeroDialog.dismiss();
            }

            updateOthers();
        }

        @Override
        public void onCardAction(Card card, int srcArea, int dstArea, Player src, Player dst) {
            mSelfWidget.updateCardArea();
            updateOthers();
            mDeskCard.updateViews(dst);
        }

        @Override
        public void onDeskClean() {
            mDeskCard.updateViews(null);
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
            } else {
                updateOthers();
            }
        }

        @Override
        public void onNetworkError() {
        }
    };

}
