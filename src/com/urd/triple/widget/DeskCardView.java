package com.urd.triple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.RelativeLayout;

import com.urd.triple.core.GameCore;

public class DeskCardView extends RelativeLayout {
    private Gallery mDeskCardsArea;
    private CardAdapter mCardAdpter;

    public DeskCardView(Context context) {
        this(context, null);
    }

    public DeskCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeskCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setupViews();
    }

    private void setupViews() {
        mDeskCardsArea = new Gallery(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mDeskCardsArea, params);
        mCardAdpter = new CardAdapter();
        mDeskCardsArea.setAdapter(mCardAdpter);
    }

    public void updateViews() {
        mCardAdpter.setHandCard(GameCore.getInstance().getDeskCards());
        mDeskCardsArea.setSelection(GameCore.getInstance().getDeskCards().size());// 选中最后一张
    }

}
