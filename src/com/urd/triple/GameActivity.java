package com.urd.triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.GameCore.GameListener;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.core.Role;
import com.urd.triple.core.commands.CardAction;
import com.urd.triple.widget.DeskCardView;
import com.urd.triple.widget.HeroListAdapter;
import com.urd.triple.widget.HeroView;
import com.urd.triple.widget.OthersWidget;
import com.urd.triple.widget.SelfWidget;

public class GameActivity extends BaseActivity {
    private static final Logger LOG = LoggerFactory.getLogger(GameActivity.class);

    private SelfWidget mSelfWidget;
    private List<OthersWidget> mOthersWidgetList;
    private DeskCardView mDeskCard;
    private GameCore mGameCore;
    private Dialog mHeroListDialog;
    private Dialog mHeroDetailDialog;

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

        if (!(mGameCore.isAllPlayerHeroSelected())) {
            showHeroList();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("是否退出游戏?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        mGameCore.close();
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void showHeroList() {
        Player self = mGameCore.getSelf();
        if (self.hero == Hero.UNKNOWN) {
            List<Integer> heroes = self.heroes;
            if (heroes.size() > 0) {
                showHeroListDialog(heroes);
            } else {
                showHeroProgressDialog("正在等待主公选武将...");
            }
        } else {
            showHeroProgressDialog("正在等待其他玩家选武将...");
        }
    }

    private void hideHeroList() {
        if (mHeroListDialog != null) {
            if (mHeroListDialog.isShowing()) {
                mHeroListDialog.dismiss();
            }
            mHeroListDialog = null;
        }
        if (mHeroDetailDialog != null) {
            if (mHeroDetailDialog.isShowing()) {
                mHeroDetailDialog.dismiss();
            }
            mHeroDetailDialog = null;
        }
    }

    private void showHeroListDialog(Collection<Integer> heroes) {
        if (mHeroListDialog == null || mHeroListDialog instanceof ProgressDialog) {
            hideHeroList();

            final HeroListAdapter adapter = new HeroListAdapter(heroes);
            mHeroListDialog = new AlertDialog.Builder(this).setAdapter(adapter,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Hero hero = adapter.getItem(which);
                            mHeroDetailDialog = (new AlertDialog.Builder(GameActivity.this))
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
                                    .create();
                            mHeroDetailDialog.show();
                        }
                    }).create();
            mHeroListDialog.setCancelable(false);
            mHeroListDialog.setCanceledOnTouchOutside(false);
        }
        if (!(mHeroDetailDialog != null && mHeroDetailDialog.isShowing()) && !(mHeroListDialog.isShowing())) {
            mHeroListDialog.show();

            mHeroDetailDialog = null;
        }
    }

    private void showHeroProgressDialog(String msg) {
        if (mHeroListDialog != null) {
            if (mHeroListDialog instanceof ProgressDialog) {
                ((ProgressDialog) mHeroListDialog).setMessage(msg);
            } else {
                hideHeroList();
            }
        }
        if (mHeroListDialog == null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(msg);
            mHeroListDialog = dialog;
            mHeroListDialog.setCancelable(false);
            mHeroListDialog.setCanceledOnTouchOutside(false);
        }
        if (!(mHeroListDialog.isShowing())) {
            mHeroListDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSelfWidget.updateViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        GameCore.getInstance().unregisterListener(mGameListener);
    }

    private void setupViews() {
        mSelfWidget = (SelfWidget) findViewById(R.id.self);

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
        Collections.reverse(mOthersWidgetList);

        updateOthers();

        mDeskCard = (DeskCardView) findViewById(R.id.desk_card);
    }

    private void updateOtherPlayers() {
        for (OthersWidget other : mOthersWidgetList) {
            other.setPlayer(null);
        }

        List<Player> players = new ArrayList<Player>(mGameCore.getPlayers());
        int offset = players.indexOf(mGameCore.getSelf()) + 1;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player != mGameCore.getSelf()) {
                mOthersWidgetList.get(((i + 8) - offset) % 8).setPlayer(player);
            }
        }
    }

    private void updateOthers() {
        updateOtherPlayers();
    }

    private final GameListener mGameListener = new GameListener() {

        @Override
        public void onPlayerLogout(Player player) {
            updateOthers();
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
            LOG.debug("player {} select {}", player.name, Hero.valueOf(hero).name);

            if (player.isLord() && player != mGameCore.getSelf()) {
                showToast("主公(%s)选择了 %s", player.name, Hero.valueOf(hero).name);
            }

            if (mGameCore.isAllPlayerHeroSelected()) {
                hideHeroList();
            } else {
                showHeroList();
            }
            if (player == GameCore.getInstance().getSelf()) {
                mSelfWidget.updateRole();
                mSelfWidget.updateSkills();
                mSelfWidget.updateHp();
            }

            updateOthers();
        }

        @Override
        public void onCardAction(Card card, int mode, int srcArea, int dstArea, Player src, Player dst) {
            LOG.debug("card action. card={}", card);

            mSelfWidget.updateCardArea();
            updateOthers();
            mDeskCard.updateViews(dst);

            String target = "自己";
            if (dst != null && dst != src) {
                target = dst.getFullname();
            }

            switch (srcArea) {
            case Card.AREA_DECK:
                if (dstArea == Card.AREA_DESK) {
                    showToast("%s 摸了张 %s 并放到桌上", src.getFullname(), card.detail.name);
                } else {
                    showToast("%s 摸了张牌", src.getFullname());
                }
                break;

            case Card.AREA_DESK:
                showToast("%s 从桌上拿了张 %s", src.getFullname(), card.detail.name);
                break;

            case Card.AREA_EQUIP:
            case Card.AREA_JUDGE:
            case Card.AREA_HAND:
                switch (dstArea) {
                case Card.AREA_DECK_TOP:
                    showToast("%s 放了张牌到牌堆顶", src.getFullname());
                    break;

                case Card.AREA_DECK_BOTTOM:
                    showToast("%s 放了张牌到牌堆底", src.getFullname());
                    break;

                case Card.AREA_DESK:
                    showToast("%s 将 %s %s中一张 %s 丢到桌上",
                            src.getFullname(),
                            target,
                            Card.getAreaName(srcArea),
                            card.detail.name);
                    break;

                case Card.AREA_EQUIP:
                case Card.AREA_JUDGE:
                case Card.AREA_HAND:
                    String cardName = "牌";
                    if (srcArea != Card.AREA_HAND || dstArea != Card.AREA_HAND) {
                        cardName = " " + card.detail.name + " ";
                    }
                    if (mode == CardAction.MODE_GET) {
                        showToast("%s 从 %s 的 %s 获得一张%s",
                                src.getFullname(),
                                dst.getFullname(),
                                Card.getAreaName(srcArea),
                                cardName);
                    } else {
                        showToast("%s 将一张%s放到 %s 的 %s中.",
                                src.getFullname(),
                                cardName,
                                target,
                                Card.getAreaName(dstArea));
                    }
                    break;

                default:
                    break;
                }
                break;

            default:
                break;
            }
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

            showToast("%s 更新了血量为 %d", player.getFullname(), player.hp);
        }

        @Override
        public void onPlayerRole(Player player) {
            LOG.debug("on player role");

            if (player != GameCore.getInstance().getSelf()) {
                updateOthers();
            }

            showToast("%s 亮出了身份 %s", player.getFullname(), Role.getName(player.role));
        }

        @Override
        public void onNetworkError() {
            showToast("网络错误");

            mGameCore.close();

            finish();
        }
    };
}
