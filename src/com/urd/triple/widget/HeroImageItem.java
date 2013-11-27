package com.urd.triple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.urd.triple.R;
import com.urd.triple.core.CardView;

public class HeroImageItem extends RelativeLayout {
    private ImageView mCheckImage;
    private CardView mAvator;

    public HeroImageItem(Context context) {
        this(context, null);
    }

    public HeroImageItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeroImageItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.hero_item, this);
        setupViews();
    }

    private void setupViews() {
        mAvator = (CardView) findViewById(R.id.avator);
        mCheckImage = (ImageView) findViewById(R.id.cb_image);
    }

    public void setImage(int i) {
        mAvator.setCard(i);
    }

    public ImageView getCbImage() {
        return mCheckImage;
    }
}
