package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Card {

    public static final int UNKNOWN = 0;

    // 区域
    public static final int AREA_DECK = 0; // 牌堆
    public static final int AREA_DESK = 1; // 牌桌
    public static final int AREA_EQUIP = 2; // 装备区
    public static final int AREA_JUDGE = 3; // 判定区
    public static final int AREA_HAND = 4; // 手牌

    public static class Generator {

        private static final int COUNT = 108;
        private static final int LOW_WATER_MARK = 10;

        private LinkedList<Card> mUnusedCards;
        private LinkedList<Card> mUsedCards;

        public Generator() {
            mUnusedCards = new LinkedList<Card>();
            mUsedCards = new LinkedList<Card>();

            // TODO: 分配正确的ID
            for (int i = 0; i < COUNT; i++) {
                mUnusedCards.add(new Card(i + 1));
            }
            Collections.shuffle(mUnusedCards);
        }

        public Card pull() {
            return pull(1).get(0);
        }

        public List<Card> pull(int count) {
            List<Card> cards = new ArrayList<Card>(count);
            for (int i = 0; i < count; i++) {
                cards.add(mUnusedCards.poll());
            }

            if (mUnusedCards.size() < LOW_WATER_MARK) {
                Collections.shuffle(mUsedCards);
                for (Card card : mUsedCards) {
                    card.area = AREA_DECK;
                }
                mUnusedCards.addAll(mUsedCards);
                mUsedCards.clear();
            }

            return cards;
        }

        public void push(List<Card> cards) {
            mUsedCards.addAll(cards);
        }
    }

    public int id;
    public int area;

    public Card(int id) {
        this.id = id;
        this.area = AREA_DECK;
    }

    public Card(int id, int area) {
        this.id = id;
        this.area = area;
    }
}
