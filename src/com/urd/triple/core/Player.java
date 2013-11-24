package com.urd.triple.core;

import java.util.Locale;

public class Player {

    public String id;
    public String name;
    public int role;
    public int hero;

    public transient GameSocket socket;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.role = Role.UNKNOWN;
        this.hero = Hero.UNKNOWN;
    }

    public boolean isLord() {
        return this.role == Role.LORD;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "[id=%s name=%s]", id, name);
    }
}
