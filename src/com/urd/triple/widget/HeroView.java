package com.urd.triple.widget;

import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.urd.triple.utils.AssetsUtil;

public class HeroView extends ImageView {

    public HeroView(Context context) {
        this(context, null);
    }

    public HeroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HeroView(Context context, int hero) {
        super(context);

        setHero(hero);
    }

    public void setHero(int hero) {
        char type = String.valueOf(hero).charAt(0);
        String filename = String.format(Locale.US, "%c%02d.jpg", type, hero % 100);
        setImageBitmap(AssetsUtil.createBitmapFromAssets(getContext(), filename));
    }
}
