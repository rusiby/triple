package com.urd.triple.core.commands;

public class SelectHeroNotify extends Command {
    public static final int ID = 9;

    public int hero;

    public SelectHeroNotify(int hero) {
        this.hero = hero;
    }

    @Override
    public SelectHeroNotify clone() throws CloneNotSupportedException {
        SelectHeroNotify command = new SelectHeroNotify(hero);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
