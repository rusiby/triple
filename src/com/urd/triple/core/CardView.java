package com.urd.triple.core;

import android.content.Context;
import android.widget.RelativeLayout;

public class CardView extends RelativeLayout {

    private int mCard;

    public CardView(Context context) {
        super(context);
    }

    public void setCard(int card) {
        mCard = card;
    }
}
