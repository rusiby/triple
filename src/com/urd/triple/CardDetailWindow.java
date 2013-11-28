package com.urd.triple;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Player;
import com.urd.triple.core.commands.CardAction;
import com.urd.triple.widget.HeroView;

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

        if (GameCore.getInstance().getSelf() == mPlayer) {
            mAdapter.addItem(new Item("出牌/丢弃", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_DESK, null);
                }
            }));
            mAdapter.addItem(new Item("装备", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_EQUIP, mPlayer);
                }
            }));
            mAdapter.addItem(new Item("放回牌顶", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_DECK_TOP, null);
                }
            }));
            mAdapter.addItem(new Item("放回牌底", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_DECK_BOTTOM, null);
                }
            }));
            mAdapter.addItem(new Item("给牌", new Callback() {

                @Override
                public void doAction() {
                    final PlayerAdapter adapter = new PlayerAdapter();
                    AlertDialog dialog = createPlayerDialog(adapter, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Player player = adapter.getItem(which);

                            GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_HAND, player);
                            dialog.dismiss();
                        }
                    });

                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }));
            mAdapter.addItem(new Item("判定区", new Callback() {

                @Override
                public void doAction() {
                    final PlayerAdapter adapter = new PlayerAdapter();
                    AlertDialog dialog = createPlayerDialog(adapter, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Player player = adapter.getItem(which);

                            GameCore.getInstance().doCardAction(mCard, CardAction.MODE_PUT, Card.AREA_JUDGE, player);
                            dialog.dismiss();
                        }
                    });
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }
            }));
        } else {
            mAdapter.addItem(new Item("拿走", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_GET, Card.AREA_HAND, mPlayer);
                }
            }));
            mAdapter.addItem(new Item("丢弃", new Callback() {

                @Override
                public void doAction() {
                    GameCore.getInstance().doCardAction(mCard, CardAction.MODE_GET, Card.AREA_DESK, mPlayer);
                }
            }));
        }

        mAdapter.addItem(new Item("详情", new Callback() {

            @Override
            public void doAction() {
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setView(new HeroView(mContext, mCard.id))
                        .setCancelable(true)
                        .create();
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }
        }));
    }

    private AlertDialog createPlayerDialog(PlayerAdapter adapter, OnClickListener listener) {
        Builder builder = (new AlertDialog.Builder(mContext))
                .setAdapter(adapter, listener)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    public void show() {
        AlertDialog dialog = (new AlertDialog.Builder(mContext))
                .setAdapter(mAdapter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Item item = mAdapter.getItem(which);
                        item.getCallback().doAction();
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.info_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String title = getItem(position).getTitle();
            holder.title.setText(title);

            return convertView;
        }
    }

    final static class ViewHolder {
        TextView title;
    }

    private class PlayerAdapter extends BaseAdapter {
        private List<Player> mPlayers;

        public PlayerAdapter() {
            mPlayers = new ArrayList<Player>(GameCore.getInstance().getPlayers());
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Player getItem(int position) {
            return mPlayers.get(position);
        }

        @Override
        public int getCount() {
            return mPlayers.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, null);
                holder.title = (TextView) convertView.findViewById(R.id.info_item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Player player = getItem(position);
            String title = getItem(position).getFullname();
            if (GameCore.getInstance().getSelf() == player) {
                title = "[我自己]";
            }
            holder.title.setText(title);

            return convertView;
        }
    };
}
