package com.urd.triple.core.commands;

public class LogoutNotify extends Command {
    public static final int ID = 4;

    public String id;

    public LogoutNotify(String id) {
        this.id = id;
    }
}
