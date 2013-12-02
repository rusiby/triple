package com.urd.triple;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.core.GameCore;
import com.urd.triple.core.Player;

public class PlayerListAdapter extends BaseAdapter {

    private List<Player> mPlayers;

    public PlayerListAdapter() {
        update();
    }

    public void update() {
        mPlayers = new ArrayList<Player>(GameCore.getInstance().getPlayers());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPlayers.size();
    }

    @Override
    public Player getItem(int position) {
        return mPlayers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        }
        Player player = mPlayers.get(position);
        String name = player.name;
        if (GameCore.getInstance().getSelf() == player) {
            int count = GameCore.getInstance().getClientCount() - 1;

            String countStr = "*";
            if (count > 0) {
                countStr = String.valueOf(count);
            }
            name += String.format(Locale.US, " [%s]", countStr);
        }
        ((TextView) convertView).setText(name);

        return convertView;
    }
}
