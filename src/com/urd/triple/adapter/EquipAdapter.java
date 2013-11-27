package com.urd.triple.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.urd.triple.core.Card;

public final class EquipAdapter extends BaseAdapter {

    private List<Card> mEquips;

    public void setEquips(List<Card> equips) {
        mEquips = equips;
    }

    @Override
    public int getCount() {
        return mEquips == null ? 0 : mEquips.size();
    }

    @Override
    public Card getItem(int position) {
        return mEquips == null ? null : mEquips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

        } else {

        }
        return null;
    }
}
