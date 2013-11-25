package com.urd.triple.core;

import java.util.Collections;
import java.util.List;

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

    public int id;
    public int area;

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

    public Card(int id) {
        this.id = id;
        this.area = AREA_DECK;
    }

    public Card(int id, int area) {
        this.id = id;
        this.area = area;
    }

    @Override
    public int compareTo(Card another) {
        return Integer.valueOf(id).compareTo(another.id);
    }
}
