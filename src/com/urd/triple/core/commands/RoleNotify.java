package com.urd.triple.core.commands;

public class RoleNotify extends Command {
    public static final int ID = 15;

    public int role;

    public RoleNotify(int role) {
        this.role = role;
    }

    @Override
    public RoleNotify clone() throws CloneNotSupportedException {
        RoleNotify command = new RoleNotify(role);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
