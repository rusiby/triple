package com.urd.triple;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Player;

public class CardDetailWindow {
    private Context mContext;
    private CardActionAdapter mAdapter;
    private Card mCard;
    private Player mPlayer;

    public CardDetailWindow(Context context, Card card, Player player) {
        this.mContext = context;
        mCard = card;
        mPlayer = player;

        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new CardActionAdapter();

        mAdapter.addItem(new Item("出牌/丢弃", new Callback() {

            @Override
            public void doAction() {
                Toast.makeText(mContext, "出牌", Toast.LENGTH_SHORT).show();

                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_DESK, mPlayer);
            }
        }));
        mAdapter.addItem(new Item("装备", new Callback() {

            @Override
            public void doAction() {
                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_EQUIP, mPlayer);
            }
        }));
        mAdapter.addItem(new Item("放回牌顶", new Callback() {

            @Override
            public void doAction() {
                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_DECK_TOP, null);
            }
        }));
        mAdapter.addItem(new Item("放回牌底", new Callback() {

            @Override
            public void doAction() {
                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_DECK_BOTTOM, null);
            }
        }));
        mAdapter.addItem(new Item("给牌", new Callback() {

            @Override
            public void doAction() {
                // TODO 实现选择目标
                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_DESK, null);
            }
        }));
        mAdapter.addItem(new Item("判定区", new Callback() {

            @Override
            public void doAction() {
                // TODO 实现选择目标
                GameCore.getInstance().doCardAction(mCard.id, Card.AREA_HAND, Card.AREA_DESK, null);
            }
        }));
        mAdapter.addItem(new Item("详情", new Callback() {

            @Override
            public void doAction() {
                // TODO 展示牌的详细
            }
        }));
    }

    public void show() {
        Builder builder = new Builder(mContext);
        ListView listview = (ListView) LayoutInflater.from(mContext).inflate(R.layout.popmenu_list, null);
        final AlertDialog dialog = builder.setView(listview).create();
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CardActionAdapter adapter = (CardActionAdapter) parent.getAdapter();
                Item item = adapter.getItem(position);
                item.getCallback().doAction();

                dialog.dismiss();
            }
        });
        listview.setAdapter(mAdapter);
        builder.setMessage("操作");
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
    }

    public static interface Callback {
        public void doAction();
    }

    private static final class Item {
        private String name;
        private Callback callback;

        public Item(String name, Callback callback) {
            this.name = name;
            this.callback = callback;
        }

        public String getTitle() {
            return name;
        }

        public Callback getCallback() {
            return callback;
        }
    }

    private static final class CardActionAdapter extends BaseAdapter {
        List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.size();
        }

        @Override
        public Item getItem(int position) {
            return items == null ? null : items.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, null);
            TextView tv = (TextView) convertView.findViewById(R.id.info_item);
            tv.setText(getItem(position).getTitle());

            return convertView;
        }
    }
}
