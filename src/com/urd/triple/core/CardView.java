package com.urd.triple.core;

import java.util.Locale;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.utils.AssetsUtil;

public class CardView extends RelativeLayout {

    private int mCard;
    private Context mContext;

    public CardView(Context context) {
        super(context);

        mContext = context;
    }

    public void setCard(int card) {
        mCard = card;

        LayoutInflater.from(getContext()).inflate(R.layout.card_pic, this);

        init();
    }

    private void init() {
        String strCard = String.format(Locale.US, "%d", mCard);
        char type = strCard.charAt(0);
        // 取得大图
        String picName = String.format(Locale.US, "%c%02d.jpg", type, mCard % 100);
        Drawable pic = AssetsUtil.ceateDrawableFromAssets(mContext, picName);

        setBackgroundDrawable(pic);
        if (type == '1') {
            // 普通牌，加载花色
            String suit = Card.calcSuit(mCard);

            // 取得花色
            TextView suitText = (TextView) findViewById(R.id.card_suit);
            suitText.setText(suit);

            if (suit.equals("♦") || suit.equals("♥")) {
                suitText.setTextColor(android.graphics.Color.RED);
            } else {
                suitText.setTextColor(android.graphics.Color.BLACK);
            }

            // 获得数字
            String point = Card.calcPoint(mCard);
            TextView pointText = (TextView) findViewById(R.id.card_points);
            pointText.setText(point);
        }
    }
}
