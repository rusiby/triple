package com.urd.triple.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.core.Card;

public final class OthersEquipAdapter extends BaseAdapter {

    private List<Card> mEquips;
    private LayoutInflater mInflater;

    public OthersEquipAdapter(Context context) {
        this(context, null);
    }

    public OthersEquipAdapter(Context context, List<Card> equips) {
        mEquips = equips;
        if (mEquips == null) {
            mEquips = new ArrayList<Card>();
        }
        mInflater = LayoutInflater.from(context);
    }

    public void updateEquips(List<Card> equips) {
        mEquips.clear();
        if (equips != null && equips.size() > 0) {
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.others_equipment_card_item, null);

            holder = new ViewHolder();
            holder.equipment = (TextView) convertView.findViewById(R.id.tv_equipment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.equipment.setText(mEquips.get(position).detail.shortName);
        return convertView;
    }

    static class ViewHolder {
        TextView equipment;
    }
}
