package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.SparseArray;

public class Card implements Comparable<Card> {

    public static final int UNKNOWN = 0;

    // 区域
    public static final int AREA_DECK = 0; // 牌堆
    public static final int AREA_DESK = 1; // 牌桌
    public static final int AREA_EQUIP = 2; // 装备区
    public static final int AREA_JUDGE = 3; // 判定区
    public static final int AREA_HAND = 4; // 手牌

    public static final int AREA_DECK_TOP = 5; // 牌堆顶
    public static final int AREA_DECK_BOTTOM = 6; // 牌堆底

    public static class Detail {

        public int id;
        public String name;
        public String fullname;

    }

    public int id;
    public int area;
    public transient Detail detail;

    public static Card find(List<Card> cards, int card) {
        int index = Collections.binarySearch(cards, new Card(card));

        return index >= 0 ? cards.get(index) : null;
    }

    public static Card remove(List<Card> cards, int card) {
        Card result = find(cards, card);
        if (result != null) {
            cards.remove(result);
        }

        return result;
    }

    public static List<Card> getCards() {
        List<Card> cards = new ArrayList<Card>();

        for (int i = 0; i < DETAIL_MAP.size(); i++) {
            cards.add(new Card(DETAIL_MAP.valueAt(i)));
        }

        return cards;
    }

    public Card(int id) {
        this.id = id;
        this.area = AREA_DECK;
        this.detail = DETAIL_MAP.get(id);
    }

    public Card(int id, int area) {
        this.id = id;
        this.area = area;
        this.detail = DETAIL_MAP.get(id);
    }

    public Card(Detail detail) {
        this.id = detail.id;
        this.area = AREA_DECK;
        this.detail = detail;
    }

    @Override
    public int compareTo(Card another) {
        return Integer.valueOf(id).compareTo(another.id);
    }

    private static void map(int card, String name) {
        Detail detail = new Detail();
        detail.id = card;
        detail.name = name;
        // TODO: 名称加入花色及点数
        detail.fullname = name;
        DETAIL_MAP.append(card, detail);
    }

    private static final SparseArray<Detail> DETAIL_MAP;

    static {
        DETAIL_MAP = new SparseArray<Card.Detail>();
        // TODO: 普通游戏牌定义（不包括武将等）
        map(1403005, "桃");
    }
}
