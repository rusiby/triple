package com.urd.triple.widget;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.urd.triple.CardDetailWindow;
import com.urd.triple.PlayerDetailWindow;
import com.urd.triple.R;
import com.urd.triple.adapter.EquipAdapter;
import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.core.Role;
import com.urd.triple.utils.AssetsUtil;

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
    private Button mClear;
    private Button mShowRole;
    private Button mToDesk;
    private Gallery mhandCardsArea;
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

        mPlayer = GameCore.getInstance().getSelf();
    }

    @Override
    protected void onFinishInflate() {
        setupViews();

        updateRole();
    }

    private void setupViews() {
        mSkill1 = (TextView) findViewById(R.id.skill_1);
        mSkill2 = (TextView) findViewById(R.id.skill_2);
        mRole = (TextView) findViewById(R.id.role);
        mAvator = (ImageView) findViewById(R.id.avator);
        mAvator.setOnClickListener(mAvatorClickListener);
        mGetCard = (Button) findViewById(R.id.get_card);
        mGetCard.setOnClickListener(mGetCardListener);
        mHp = (TextView) findViewById(R.id.hp);
        mIncreaseHp = (Button) findViewById(R.id.hp_increase);
        mIncreaseHp.setOnClickListener(mHpChangeListener);
        mDecreaseHp = (Button) findViewById(R.id.hp_decrease);
        mDecreaseHp.setOnClickListener(mHpChangeListener);
        mhandCardsArea = (Gallery) findViewById(R.id.cards);
        mhandCardsArea.setAdapter(new CardAdapter());
        mhandCardsArea.setOnItemClickListener(mOnCardClickListener);
        mEquipList = (ListView) findViewById(R.id.equipment_list);
        mEquipList.setAdapter(new EquipAdapter());
        mEquipList.setOnItemClickListener(mEquipItemClick);

        mJudgeHappy = (TextView) findViewById(R.id.happy);
        mJudgeThunder = (TextView) findViewById(R.id.thunder);
        mClear = (Button) findViewById(R.id.clear);
        mClear.setOnClickListener(onClearDeskListener);
        mShowRole = (Button) findViewById(R.id.show_indentity);
        mShowRole.setOnClickListener(onShowRoleClickListener);
        mToDesk = (Button) findViewById(R.id.to_desk);
        mToDesk.setOnClickListener(mGetCardListener);
    }

    public void updateViews() {
        if (mPlayer.hero != Hero.UNKNOWN) {
            updateCardArea();
            updateHp();
            updateSkills();
        }
        if (mPlayer.role != Role.UNKNOWN) {
            updateRole();
        }
    }

    public void updateCardArea() {
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

        updateHandCardArea(handCards);
        updateEquipArea(equipCards);
        updateJudgeCardArea(judgeCards);
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
        String info = "";
        if (mPlayer.hero != Hero.UNKNOWN) {
            info += Hero.valueOf(mPlayer.hero).name + " ";

            updateAvator(mPlayer.hero);
        }
        info += Role.getName(mPlayer.role);
        mRole.setText(info);
    }

    private void updateAvator(int heroId) {
        Drawable drawable = AssetsUtil.ceateDrawableFromAssets(getContext(), Hero.valueOf(heroId).portraitPic);
        mAvator.setImageDrawable(drawable);
    }

    public void updateSkills() {
        if (mPlayer.hero != Hero.UNKNOWN) {
            Hero hero = Hero.valueOf(mPlayer.hero);
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

    private void showCardDetailWindow(Card card) {
        CardDetailWindow window = new CardDetailWindow(getContext(), card, mPlayer);
        window.show();
    }

    private void showPlayerDetailWindow() {
        PlayerDetailWindow window = new PlayerDetailWindow(getContext(), getRootView(), mPlayer);
        window.show();
    }

    private final OnClickListener mGetCardListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.get_card:
                GameCore.getInstance().doCardAction(null, Card.AREA_HAND, null);
                break;

            case R.id.to_desk:
                GameCore.getInstance().doCardAction(null, Card.AREA_DESK, null);
                break;

            default:
                break;
            }

        }
    };

    private OnClickListener onClearDeskListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mPlayer.isLord()) {
                Builder builder = new Builder(getContext()).setMessage("提示").setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                GameCore.getInstance().cleanDesk();

                                dialog.dismiss();
                            }
                        });

                builder.create().show();
            } else {
                Toast.makeText(getContext(), "只有主公才可以清台", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private OnClickListener onShowRoleClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Builder builder = new Builder(getContext()).setMessage("提示").setNegativeButton("取消", null)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GameCore.getInstance().showRole();

                            dialog.dismiss();
                        }
                    });

            builder.create().show();
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

    private OnClickListener mAvatorClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            showPlayerDetailWindow();
        }
    };

    private OnItemClickListener mOnCardClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CardAdapter adapter = (CardAdapter) parent.getAdapter();
            Card card = adapter.getItem(position);

            showCardDetailWindow(card);
        }
    };

    private OnItemClickListener mEquipItemClick = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EquipAdapter adapter = (EquipAdapter) parent.getAdapter();
            Card card = adapter.getItem(position);

            showCardDetailWindow(card);
        }
    };

}
