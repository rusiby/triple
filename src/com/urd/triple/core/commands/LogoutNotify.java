package com.urd.triple.core.commands;

public class LogoutNotify extends Command {
    public static final int ID = 4;

    public String id;

    public LogoutNotify(String id) {
        this.id = id;
    }

    @Override
    public LogoutNotify clone() throws CloneNotSupportedException {
        LogoutNotify command = new LogoutNotify(id);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
