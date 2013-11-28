package com.urd.triple.widget;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urd.triple.PlayerDetailWindow;
import com.urd.triple.R;
import com.urd.triple.adapter.OthersEquipAdapter;
import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.core.Role;

public class OthersWidget extends RelativeLayout {
    private Player mPlayer;
    private TextView mIdentity;
    private TextView mJudgeThunder;
    private TextView mJudgeHappy;
    private TextView mHpsCount;
    private TextView mCardsCount;
    private TextView mSkill;
    private GridView mEquipmentContainer;
    private OthersEquipAdapter mAdapter;
    private RelativeLayout mLayout;

    public OthersWidget(Context context) {
        this(context, null);
    }

    public OthersWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OthersWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.others_view, this);
    }

    @Override
    protected void onFinishInflate() {
        setupViews();
    }

    private void setupViews() {
        mLayout = (RelativeLayout) findViewById(R.id.rlayout_others);
        mLayout.setOnClickListener(mOnClickLayoutListener);

        mIdentity = (TextView) findViewById(R.id.tv_identity);
        mJudgeThunder = (TextView) findViewById(R.id.tv_judge01);
        mJudgeHappy = (TextView) findViewById(R.id.tv_judge02);
        mHpsCount = (TextView) findViewById(R.id.tv_hp_count);
        mCardsCount = (TextView) findViewById(R.id.tv_cards_count);
        mSkill = (TextView) findViewById(R.id.tv_skills);

        mEquipmentContainer = (GridView) findViewById(R.id.gv_equipment_container);
        mAdapter = new OthersEquipAdapter(getContext());
        mEquipmentContainer.setAdapter(mAdapter);
    }

    public void setPlayer(Player player) {
        mPlayer = player;

        if (mPlayer != null) {
            update();
        } else {
            reset();
        }
    }

    private void reset() {
        mIdentity.setText("空位");
        mAdapter.updateEquips(null);
        mHpsCount.setText("0");
        mCardsCount.setText("0");
        mSkill.setText("");
        mJudgeThunder.setText("");
        mJudgeHappy.setText("");
        mLayout.setVisibility(View.INVISIBLE);
    }

    public void update() {
        if (mPlayer != null) {
            mLayout.setVisibility(View.VISIBLE);
            updateIdentity(mPlayer.role);
            updateCardArea();
            updateHpCount();
            updateSkills();
            mLayout.setSelected(false);
        }
    }

    private void updateCardArea() {
        List<Card> cards = mPlayer.cards;
        List<Card> handCards = new ArrayList<Card>();
        List<Card> equipCards = new ArrayList<Card>();
        List<Card> judgeCards = new ArrayList<Card>();
        Card card = null;
        for (int i = 0, len = cards.size(); i < len; i++) {
            card = cards.get(i);
            switch (card.area) {
            case Card.AREA_HAND:
                handCards.add(card);
                break;

            case Card.AREA_EQUIP:
                equipCards.add(card);
                break;

            case Card.AREA_JUDGE:
                judgeCards.add(card);
                break;

            default:
                break;
            }
        }

        if (handCards != null) {
            updateCardCount(handCards);
        }

        if (equipCards != null) {
            updateEquipArea(equipCards);
        }
        if (judgeCards != null) {
            updateJudgeCardArea(judgeCards);
        }
    }

    public void updateJudgeCardArea(List<Card> judgeCards) {
        int happyCount = 0;
        int thunderCount = 0;

        for (int i = 0, len = judgeCards.size(); i < len; i++) {
            String name = judgeCards.get(i).detail.name;
            if (name.equals("闪电")) {
                thunderCount++;
            } else {
                happyCount++;
            }
        }

        if (happyCount > 0) {
            mJudgeHappy.setText("乐:" + happyCount);
            mJudgeHappy.setVisibility(View.VISIBLE);
        } else {
            mJudgeHappy.setText("");
            mJudgeHappy.setVisibility(View.GONE);
        }
        if (thunderCount > 0) {
            mJudgeThunder.setText("电:" + thunderCount);
            mJudgeThunder.setVisibility(View.VISIBLE);
        } else {
            mJudgeThunder.setText("");
            mJudgeThunder.setVisibility(View.GONE);
        }
    }

    private void updateCardCount(List<Card> handCards) {
        if (handCards != null && handCards.size() > 0) {
            mCardsCount.setText("" + handCards.size());
        } else {
            mCardsCount.setText("0");
        }
    }

    private void updateHpCount() {
        mHpsCount.setText("" + mPlayer.hp);
    }

    private void updateEquipArea(List<Card> equips) {
        mAdapter.updateEquips(equips);
    }

    private void updateIdentity(int role) {
        String roleName = null;
        switch (role) {
        case Role.LORD:
            roleName = "主公:";
            break;

        case Role.LOYALIST:
            roleName = "忠臣:";
            break;

        case Role.REBEL:
            roleName = "反贼:";
            break;

        case Role.TRAITOR:
            roleName = "内奸:";
            break;

        default:
            roleName = "";
            break;
        }

        String heroName = "";
        if (mPlayer.isLord() && mPlayer.hero != Hero.UNKNOWN || GameCore.getInstance().isAllPlayerHeroSelected()) {
            heroName = Hero.valueOf(mPlayer.hero).name;
        }
        String identity = String.format("%1$s %2$s %3$s", mPlayer.name, roleName, heroName);
        mIdentity.setText(identity);
    }

    private void updateSkills() {
        String skills = "";
        if (mPlayer.isLord() && mPlayer.hero != Hero.UNKNOWN || GameCore.getInstance().isAllPlayerHeroSelected()) {
            skills = StringUtils.join(Hero.valueOf(mPlayer.hero).skills, ",");
        }
        mSkill.setText(skills);
    }

    private void showPlayerDetailWindow() {
        PlayerDetailWindow window = new PlayerDetailWindow(getContext(), getRootView(), mPlayer);
        window.setOnDismissListener(mOnDismissListener);
        window.show();
    }

    private PopupWindow.OnDismissListener mOnDismissListener = new PopupWindow.OnDismissListener() {

        @Override
        public void onDismiss() {
            mLayout.setSelected(false);
        }
    };

    private final OnClickListener mOnClickLayoutListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mPlayer != null && Hero.valueOf(mPlayer.hero) != null) {
                if (!mLayout.isSelected()) {
                    mLayout.setSelected(true);

                    showPlayerDetailWindow();
                } else {
                    mLayout.setSelected(false);
                }
            }
        }
    };
}
