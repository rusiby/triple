package com.urd.triple.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.urd.triple.core.Card;

public final class OthersEquipAdapter extends BaseAdapter {

    private List<Card> mEquips;

    public OthersEquipAdapter() {
        this(null);
    }
    
    public OthersEquipAdapter(List<Card> equips) {
        mEquips = equips;
        if (mEquips == null) {
            mEquips = new ArrayList<Card>();
        }
    }
    
    public void updateEquips(List<Card> equips) {
        if (equips != null && equips.size() > 0) {
            mEquips.clear();
            mEquips.addAll(equips);
            notifyDataSetChanged();
        }
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
