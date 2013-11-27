package com.urd.triple.adapter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.R;
import com.urd.triple.core.Card;

public final class EquipAdapter extends BaseAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(EquipAdapter.class);

    private List<Card> mEquips;

    public void setEquips(List<Card> equips) {
        mEquips = equips;
        notifyDataSetChanged();
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
        LOG.debug("get view p={}", position);

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.equip_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.info_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(getItem(position).detail.name);

        return convertView;
    }

    final static class ViewHolder {
        TextView title;
    }
}
