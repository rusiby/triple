package com.urd.triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.widget.DeskCardView;
import com.urd.triple.widget.HeroListAdapter;
import com.urd.triple.widget.HeroView;
import com.urd.triple.widget.OthersWidget;
import com.urd.triple.widget.SelfWidget;

public class GameActivity extends BaseActivity {

    private SelfWidget mSelfWidget;
    private List<OthersWidget> mOthersWidgetList;
    private DeskCardView mDeskCard;
    private GameCore mGameCore;
    private Dialog mHeroListDialog;

    public static void launch(Context context, Intent intent) {
        intent.setClass(context, GameActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_activity);

        mGameCore = GameCore.getInstance();

        mGameCore.registerListener(mGameListener);

        setupViews();

        if (mGameCore.getSelf().hero == Hero.UNKNOWN) {
            showHeroList();
        }
    }

    private void showHeroList() {
        if (mHeroListDialog != null) {
            if (mHeroListDialog.isShowing()) {
                mHeroListDialog.dismiss();
            }
            mHeroListDialog = null;
        }

        List<Integer> heroes = mGameCore.getSelf().heroes;
        if (heroes.size() > 0) {
            final HeroListAdapter adapter = new HeroListAdapter(heroes);
            mHeroListDialog = new AlertDialog.Builder(this).setAdapter(adapter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final Hero hero = adapter.getItem(which);
                    ImageView iv = new ImageView(GameActivity.this);
                    iv.setImageResource(R.drawable.daqiao);
                    (new AlertDialog.Builder(GameActivity.this))
                            .setView(new HeroView(GameActivity.this, hero.id))
                            .setPositiveButton("确　定", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mGameCore.selectHero(hero.id);
                                }
                            })
                            .setNegativeButton("取　消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHeroListDialog.show();
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {

                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    mHeroListDialog.show();
                                }
                            })
                            .create()
                            .show();
                }
            }).create();
        } else {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在等待主公选武将...");
            mHeroListDialog = dialog;
        }
        mHeroListDialog.setCancelable(false);
        mHeroListDialog.setCanceledOnTouchOutside(false);
        mHeroListDialog.show();
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
            showHeroList();
        }

        @Override
        public void onPlayerHeroSelected(Player player, int hero) {
            int heroSelectedCount = 0;
            for (Player p : mGameCore.getPlayers()) {
                if (p.hero != Hero.UNKNOWN) {
                    heroSelectedCount++;
                }
            }
            if (heroSelectedCount == mGameCore.getPlayers().size()) {
                // TODO: 显示所有武将信息
            } else {
                // TODO: 仅显示主公信息
            }
            if (player == GameCore.getInstance().getSelf()) {
                mSelfWidget.updateRole();
                mSelfWidget.updateSkills();
                mSelfWidget.updateHp();
            }
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
                updateOthers();
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
