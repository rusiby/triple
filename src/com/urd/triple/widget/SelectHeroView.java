package com.urd.triple.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.core.GameCore;

public class SelectHeroView extends LinearLayout {
    private LinearLayout mSelectHeroLayout;
    private List<Integer> mHeros;

    public SelectHeroView(Context context) {
        this(context, null);
    }

    public SelectHeroView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectHeroView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.select_hero, this);
        setupViews();
    }

    private void setupViews() {
        mSelectHeroLayout = (LinearLayout) findViewById(R.id.ll_select_hero);
        Button btnOk = (Button) findViewById(R.id.btn_select_ok);
        btnOk.setOnClickListener(mOnClickListener);
    }

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            for (int j = 0; j < mSelectHeroLayout.getChildCount(); j++) {
                HeroImageItem item = (HeroImageItem) mSelectHeroLayout.getChildAt(j);
                if (item.getCbImage().getVisibility() == View.VISIBLE) {
                    Integer selectId = (Integer) item.getTag();
                    GameCore.getInstance().selectHero(Integer.valueOf(selectId));
                    break;
                }
            }
        }
    };

    public void setHeros(List<Integer> heros) {
        mHeros = heros;
        if (mHeros != null && heros.size() > 0) {
            mSelectHeroLayout.removeAllViews();
            for (int i = 0; i < heros.size(); i++) {
                addHeroImage(heros.get(i));
            }
        }else {
            TextView tipText=new TextView(getContext());
            tipText.setText("等待主公选将，请稍后");
            mSelectHeroLayout.addView(tipText);
        }
    }

    private void addHeroImage(final int i) {
        HeroImageItem imageItem = new HeroImageItem(getContext());
        imageItem.setTag(i);
        imageItem.setImage(i);
        imageItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("evan", "onClick getTag=" + v.getTag());
                for (int j = 0; j < mSelectHeroLayout.getChildCount(); j++) {
                    HeroImageItem item = (HeroImageItem) mSelectHeroLayout.getChildAt(j);
                    if (item.getTag() == (Integer) v.getTag()) {
                        item.getCbImage().setVisibility(View.VISIBLE);
                    } else {
                        item.getCbImage().setVisibility(View.GONE);
                    }
                }
            }
        });
        mSelectHeroLayout.addView(imageItem);
    }
}
