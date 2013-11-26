package com.urd.triple.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {

    private static final int LOW_WATER_MARK = 20;

    private LinkedList<Card> mUnusedCards;
    private LinkedList<Card> mUsedCards;

    public Deck() {
        mUnusedCards = new LinkedList<Card>(Card.getCards());
        mUsedCards = new LinkedList<Card>();

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
                card.area = Card.AREA_DECK;
            }
            mUnusedCards.addAll(mUsedCards);
            mUsedCards.clear();
        }

        return cards;
    }

    public void addTop(Card card) {
        card.area = Card.AREA_DECK;
        mUnusedCards.addFirst(card);
    }

    public void addBottom(Card card) {
        card.area = Card.AREA_DECK;
        mUnusedCards.addLast(card);
    }

    public void add(Card card, int area) {
        if (area == Card.AREA_DECK_TOP) {
            addTop(card);
        } else if (area == Card.AREA_DECK_BOTTOM) {
            addBottom(card);
        }
    }

    public void push(List<Card> cards) {
        mUsedCards.addAll(cards);
    }
}
