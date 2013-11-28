package com.urd.triple.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Player {

    public String id;
    public String name;
    public int role;
    public int hero;
    public int hp;
    public List<Integer> heroes;
    public List<Card> cards;

    public transient GameSocket socket;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.role = Role.UNKNOWN;
        this.hero = Hero.UNKNOWN;
        this.hp = 4;
        this.heroes = new ArrayList<Integer>();
        this.cards = new ArrayList<Card>();
    }

    public Card getCard(int card) {
        return Card.find(cards, card);
    }

    public boolean isLord() {
        return this.role == Role.LORD;
    }

    public boolean containsCard(int card) {
        return getCard(card) != null;
    }

    public boolean containsCard(Card card) {
        return getCard(card.id) != null;
    }

    public String getFullname() {
        String fullname = name;
        if (hero != Hero.UNKNOWN) {
            fullname = String.format(Locale.US, "%s(%s)", name, Hero.valueOf(hero).name);
        }

        return fullname;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[id=%s name=%s]", id, name);
    }
}
