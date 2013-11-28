package com.urd.triple.core.commands;

public class StartGameNotify extends Command {
    public static final int ID = 7;

    public int role;
    public String lordID;

    public StartGameNotify(int role, String lordID) {
        this.role = role;
        this.lordID = lordID;
    }

    @Override
    public StartGameNotify clone() throws CloneNotSupportedException {
        StartGameNotify command = new StartGameNotify(role, lordID);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
