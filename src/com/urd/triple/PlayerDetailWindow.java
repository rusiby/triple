package com.urd.triple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.urd.triple.core.Card;
import com.urd.triple.core.Hero;
import com.urd.triple.core.Player;
import com.urd.triple.widget.HeroView;

public class PlayerDetailWindow {
    private View mRoot;
    private View mParent;
    private Context mContext;
    private PopupWindow mWindow;
    private PlayerInfoAdapter mAdapter;
    private Player mPlayer;

    private int mDefaultWidth;

    public PlayerDetailWindow(Context context, View parent, Player player) {
        this.mContext = context;
        this.mParent = parent;
        mPlayer = player;
        mWindow = new PopupWindow(context);
        mDefaultWidth = mContext.getResources().getDimensionPixelSize(R.dimen.pupup_menu_width);
        mWindow.setBackgroundDrawable(new ColorDrawable());
        mWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mWindow.dismiss();

                    return true;
                }

                return false;
            }
        });

        initAdapter();
    }

    private void initAdapter() {
        mAdapter = new PlayerInfoAdapter();
        String skills = Hero.valueOf(mPlayer.hero).name + " ";
        List<String> list = Hero.valueOf(mPlayer.hero).skills;
        for (int i = 0, len = list.size(); i < len; i++) {
            if (!TextUtils.isEmpty(list.get(i))) {
                skills = skills + "技能" + i + ":" + list.get(i) + "    ";
            }
        }

        List<Card> cards = mPlayer.cards;
        List<Card> handCards = new ArrayList<Card>();
        List<Card> equipCards = new ArrayList<Card>();
        List<Card> judgeCards = new ArrayList<Card>();
        Card card = null;
        for (int i = 0, len = cards.size(); i < len; i++) {
            card = cards.get(i);
            switch (card.area) {
            case Card.AREA_HAND:
                Card temp = new Card(card.id, card.area);
                handCards.add(temp);
                Collections.shuffle(handCards);
                break;

            case Card.AREA_EQUIP:
                equipCards.add(card);
                break;
            case Card.AREA_JUDGE:
                judgeCards.add(card);
                break;

            default:
                break;
            }
        }
        mAdapter.addItem(new Item(skills, null));

        mAdapter.addItem(new Item("手牌", null));
        mAdapter.addItems(handCards);

        mAdapter.addItem(new Item("装备", null));
        mAdapter.addItems(equipCards);

        mAdapter.addItem(new Item("判断", null));
        mAdapter.addItems(judgeCards);
    }

    public void show() {
        ListView listview = (ListView) LayoutInflater.from(mContext).inflate(R.layout.popmenu_list, null);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    showHeroDetailWindow(mPlayer);
                } else {
                    PlayerInfoAdapter adapter = (PlayerInfoAdapter) parent.getAdapter();
                    Item item = adapter.getItem(position);
                    showCardDetailWindow(item.getCard());
                }
                dismiss();
            }
        });
        listview.setAdapter(mAdapter);
        setContentView(listview);
        preShow();

        // mWindow.showAsDropDown(mAnchor);
        mWindow.showAtLocation(mParent, Gravity.CENTER, 0, 0);
    }

    protected void showHeroDetailWindow(Player player) {
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(new HeroView(mContext, player.hero))
                .create();

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showCardDetailWindow(Card card) {
        CardDetailWindow window = new CardDetailWindow(mContext, card, mPlayer);
        window.show();
    }

    protected void preShow() {
        if (mRoot == null) {
            throw new IllegalStateException("setContentView was not called");
        }

        mWindow.setWidth(mDefaultWidth);
        mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);

        mWindow.setContentView(mRoot);
    }

    public void setWidth(int widthPixel) {
        mDefaultWidth = widthPixel;
    }

    public void setContentView(View root) {
        mRoot = root;
        mWindow.setContentView(root);
    }

    public void setContentView(int layoutResID) {
        LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setContentView(inflator.inflate(layoutResID, null));
    }

    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        mWindow.setOnDismissListener(listener);
    }

    public void dismiss() {
        mWindow.dismiss();
    }

    private static final class Item {
        private String name;
        private Card card;

        public Item(String name, Card card) {
            this.name = name;
            this.card = card;
        }

        public String getTitle() {
            return name;
        }

        public Card getCard() {
            return card;
        }
    }

    private static final class PlayerInfoAdapter extends BaseAdapter {
        List<Item> items = new ArrayList<Item>();

        public void addItem(Item item) {
            items.add(item);
        }

        public void addItems(List<Card> cards) {
            for (int i = 0, len = cards.size(); i < len; i++) {
                String name = null;
                Card card = cards.get(i);
                if (card.area != Card.AREA_HAND) {
                    name = card.detail.fullname;
                } else {
                    name = "手牌 " + i;
                }
                addItem(new Item(name, card));
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return position == 0 || items.get(position).getCard() != null;
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
            int paddingLeft = 0;
            if (isEnabled(position)) {
                holder.title.setTextColor(parent.getContext().getResources().getColor(R.color.item));
                paddingLeft = 10;
            } else {
                holder.title.setTextColor(parent.getContext().getResources().getColor(R.color.item_group));
                paddingLeft = 20;
            }

            holder.title.setPadding(paddingLeft, 0, 0, 0);

            return convertView;
        }
    }

    final static class ViewHolder {
        TextView title;
    }
}
