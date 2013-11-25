package com.urd.triple.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.SparseArray;

public class Player {

    public String id;
    public String name;
    public int role;
    public int hero;
    public List<Card> handCards;// 手牌
    public SparseArray<Card> equipments;// 装备区的牌
    public SparseArray<List<Card>> judges;// 判断区的牌

    public transient GameSocket socket;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.role = Role.UNKNOWN;
        this.hero = Hero.UNKNOWN;
        handCards = new ArrayList<Card>();
        equipments = new SparseArray<Card>();
        judges = new SparseArray<List<Card>>();
    }

    public void addJudgeThunder(Card card) {
        addJudge(card, Judgement.JUDGE_THUNDER);
    }

    public void addJudgeHappy(Card card) {
        addJudge(card, Judgement.JUDGE_HAPPY);
    }

    public void removeJudgeThunder(Card card) {
        removeJudge(card, Judgement.JUDGE_THUNDER);
    }

    public void removeJudgeHappy(Card card) {
        removeJudge(card, Judgement.JUDGE_HAPPY);
    }

    public void removeDefentHorse() {
        equipments.remove(Equipment.DEFENT_HORSE);
    }

    public void removeShield() {
        equipments.remove(Equipment.SHIELD);
    }

    public void removeSword() {
        equipments.remove(Equipment.SWORD);
    }

    public void removeAttackHorse() {
        equipments.remove(Equipment.ATTACK_HORSE);
    }

    public void equipDefentHorse(Card card) {
        equipments.put(Equipment.DEFENT_HORSE, card);
    }

    public void equipShield(Card card) {
        equipments.put(Equipment.SHIELD, card);
    }

    public void equipSword(Card card) {
        equipments.put(Equipment.SWORD, card);
    }

    public void equipAttackHorse(Card card) {
        equipments.put(Equipment.ATTACK_HORSE, card);
    }

    public boolean isLord() {
        return this.role == Role.LORD;
    }

    public void addCard(Card card) {
        handCards.add(card);
    }

    public void removeCard(Card card) {
        handCards.remove(card);
    }

    private void addJudge(Card card, int type) {
        List<Card> list = judges.get(type);
        if (list == null) {
            list = new ArrayList<Card>();
        }
        list.add(card);
        judges.put(type, list);
    }

    private void removeJudge(Card card, int type) {
        List<Card> list = judges.get(type);
        if (list != null) {
            list.remove(card);
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[id=%s name=%s]", id, name);
    }
}
