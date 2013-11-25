package com.urd.triple.core.commands;

public class ChangeHPNotify extends Command {
    public static final int ID = 14;

    public int hp;

    public ChangeHPNotify(int hp) {
        this.hp = hp;
    }
}
