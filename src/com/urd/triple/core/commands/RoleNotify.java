package com.urd.triple.core.commands;

public class RoleNotify extends Command {
    public static final int ID = 15;

    public int role;

    public RoleNotify(int role) {
        this.role = role;
    }
}
