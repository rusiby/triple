package com.urd.triple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.core.Player;
import com.urd.triple.core.Role;
import com.urd.triple.core.commands.CardActionReq;

public class SelfWidget extends RelativeLayout {
    private Player mPlayer;
    private TextView mRole;
    private ImageView mAvator;
    private Button mGetCard;

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
    }

    public void setPlayer(Player self) {
        mPlayer = self;

        updateViews();
    }

    private void updateViews() {
        updateRole(mPlayer.role);
        updateAvator(mPlayer.hero);
    }

    private void updateRole(int role) {
        String roleName = null;
        switch (role) {
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

    private void updateAvator(int hero) {
        // TODO 更具武将的id加载武将的头像
    }

    private void updateSkills(int hero) {
        // TODO 更新技能
    }

    private final OnClickListener mGetCardListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO 摸牌处理
            mPlayer.socket.send(new CardActionReq());
        }
    };
}
