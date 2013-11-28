package com.urd.triple.core.commands;

public class CleanDeskNotify extends Command {
    public static final int ID = 13;

    @Override
    public CleanDeskNotify clone() throws CloneNotSupportedException {
        CleanDeskNotify command = new CleanDeskNotify();
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
