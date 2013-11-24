package com.urd.triple.core.commands;

public class StartGameNotify extends Command {
    public static final int ID = 7;

    public int role;
    public String lordID;

    public StartGameNotify(int role, String lordID) {
        this.role = role;
        this.lordID = lordID;
    }
}
