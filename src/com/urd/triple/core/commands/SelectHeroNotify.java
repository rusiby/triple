package com.urd.triple.core.commands;

public class SelectHeroNotify extends Command {
    public static final int ID = 9;

    public int hero;

    public SelectHeroNotify(int hero) {
        this.hero = hero;
    }
}
