package com.urd.triple.core.commands;

import java.util.List;

public class HeroListNotify extends Command {
    public static final int ID = 8;

    public List<Integer> heroes;

    public HeroListNotify(List<Integer> heroes) {
        this.heroes = heroes;
    }

    @Override
    public HeroListNotify clone() throws CloneNotSupportedException {
        HeroListNotify command = new HeroListNotify(heroes);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
