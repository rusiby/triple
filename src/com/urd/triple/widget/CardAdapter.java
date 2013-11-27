package com.urd.triple.widget;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.urd.triple.core.Card;

final class CardAdapter extends PagerAdapter {

    private List<Card> mHandcard;

    public void setHandCard(List<Card> list) {
        mHandcard = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mHandcard == null ? 0 : mHandcard.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // PosterView poster = new PosterView(container.getContext());
        ImageView img = new ImageView(container.getContext());
        img.setImageDrawable(CardAdapter.getCardImage(mHandcard.get(position)));
        container.addView(img);

        return img;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ImageView view = (ImageView) object;

        container.removeView(view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    private static Drawable getCardImage(Card card) {
        return null;
    }

}
