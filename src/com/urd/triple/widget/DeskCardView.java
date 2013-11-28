package com.urd.triple.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.RelativeLayout;

import com.urd.triple.core.Card;
import com.urd.triple.core.GameCore;
import com.urd.triple.core.Player;
import com.urd.triple.core.commands.CardAction;

public class DeskCardView extends RelativeLayout implements OnItemClickListener {
    private Gallery mDeskCardsArea;
    private CardAdapter mCardAdpter;

    private Player mPlayer;

    public DeskCardView(Context context) {
        this(context, null);
    }

    public DeskCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeskCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        setupViews();
    }

    private void setupViews() {
        mDeskCardsArea = new Gallery(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mDeskCardsArea, params);
        mCardAdpter = new CardAdapter();
        mDeskCardsArea.setAdapter(mCardAdpter);
        mDeskCardsArea.setOnItemClickListener(this);
    }

    public void updateViews(Player player) {
        mPlayer = player;
        mCardAdpter.setHandCard(GameCore.getInstance().getDeskCards());
        mDeskCardsArea.setSelection(GameCore.getInstance().getDeskCards().size());// 选中最后一张
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CardAdapter adapter = (CardAdapter) parent.getAdapter();
        Card card = adapter.getItem(position);

        showOption(card);
    }

    private void showOption(final Card card) {
        final String[] array = new String[] { "拿走", "详情" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    GameCore.getInstance().doCardAction(card, CardAction.MODE_GET, Card.AREA_HAND, null);
                } else {
                    AlertDialog detail = new AlertDialog.Builder(getContext())
                            .setView(new HeroView(getContext(), card.id))
                            .setCancelable(true)
                            .create();
                    detail.setCanceledOnTouchOutside(true);
                    detail.show();
                }
            }
        }).create().show();
    }

}
