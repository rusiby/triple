package com.urd.triple.widget;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.urd.triple.core.Card;
import com.urd.triple.core.CardView;

final class CardAdapter extends BaseAdapter {

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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CardView(parent.getContext());
        }

        ((CardView) convertView).setCard(getItem(position).id);

        return convertView;
    }

    @Override
    public Card getItem(int position) {
        return mHandcard == null ? null : mHandcard.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
