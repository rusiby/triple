package com.urd.triple.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.adapter.EquipAdapter;
import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.core.Role;

public class SelfWidget extends RelativeLayout {
    private Player mPlayer;
    private TextView mRole;
    private TextView mHp;
    private TextView mJudgeHappy;
    private TextView mJudgeThunder;
    private TextView mSkill1;
    private TextView mSkill2;
    private ImageView mAvator;
    private Button mGetCard;
    private Button mIncreaseHp;
    private Button mDecreaseHp;
    private ViewPager mhandCardsArea;
    private ListView mEquipList;

    public SelfWidget(Context context) {
        this(context, null);
    }

    public SelfWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelfWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.self_view, this);
    }

    @Override
    protected void onFinishInflate() {
        setupViews();
    }

    private void setupViews() {
        mRole = (TextView) findViewById(R.id.role);
        mAvator = (ImageView) findViewById(R.id.avator);
        mGetCard = (Button) findViewById(R.id.get_card);
        mGetCard.setOnClickListener(mGetCardListener);
        mHp = (TextView) findViewById(R.id.hp);
        mIncreaseHp = (Button) findViewById(R.id.hp_increase);
        mIncreaseHp.setOnClickListener(mHpChangeListener);
        mDecreaseHp = (Button) findViewById(R.id.hp_decrease);
        mDecreaseHp.setOnClickListener(mHpChangeListener);
        mhandCardsArea = (ViewPager) findViewById(R.id.cards);
        mhandCardsArea.setAdapter(new CardAdapter());
        mEquipList = (ListView) findViewById(R.id.equipment_list);
        mEquipList.setAdapter(new EquipAdapter());

        mJudgeHappy = (TextView) findViewById(R.id.happy);
        mJudgeThunder = (TextView) findViewById(R.id.thunder);
    }

    public void setPlayer(Player self) {
        mPlayer = self;

        updateViews();
    }

    private void updateViews() {
        updateRole();
        updateAvator();
        updateSkills();
        updateHp();
        updateCardArea();
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
            updateHandCardArea(handCards);
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
            if (name.equals("乐不思蜀")) {
                happyCount++;
            } else if (name.equals("闪电")) {
                thunderCount++;
            }
        }
        mJudgeHappy.setText("乐不思蜀：" + happyCount);
        mJudgeThunder.setText("闪电：" + thunderCount);
    }

    private void updateHandCardArea(List<Card> handCards) {
        CardAdapter adapter = (CardAdapter) mhandCardsArea.getAdapter();
        adapter.setHandCard(handCards);
    }

    private void updateEquipArea(List<Card> equips) {
        EquipAdapter adapter = (EquipAdapter) mEquipList.getAdapter();
        adapter.setEquips(equips);
    }

    public void updateRole() {
        String roleName = null;
        switch (mPlayer.role) {
        case Role.LORD:
            roleName = "主公";
            break;

        case Role.LOYALIST:
            roleName = "忠臣";
            break;

        case Role.REBEL:
            roleName = "反贼";
            break;

        case Role.TRAITOR:
            roleName = "内奸";
            break;

        default:
            roleName = "error";
            break;
        }

        mRole.setText(roleName);
    }

    private void updateAvator() {
        // TODO 更具武将的id加载武将的头像
        // mAvator.setImageResource(0);
    }

    private void updateSkills() {
        // TODO 更新技能
        Hero hero = Hero.valueOf(mPlayer.hero);

        if (hero != null) {
            List<String> skills = hero.skills;
            mSkill1.setText(skills.get(0));
            if (skills.size() > 1) {// 目前最多两个技能
                mSkill2.setText(skills.get(1));
            }
        }
    }

    public void updateHp() {
        mHp.setText(String.valueOf(mPlayer.hp));
    }

    public Drawable getCardImage(Card card) {
        // TODO更具card的id获取一个drawable
        return null;
    }

    private final OnClickListener mGetCardListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            GameCore.getInstance().doCardAction(Card.UNKNOWN, Card.AREA_DECK, Card.AREA_HAND, null);
        }
    };

    private final OnClickListener mHpChangeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int hp = mPlayer.hp;
            switch (v.getId()) {
            case R.id.hp_increase:
                hp++;
                break;

            case R.id.hp_decrease:
                hp--;
                break;

            default:
                break;
            }

            GameCore.getInstance().changeHP(hp);
        }
    };
}
