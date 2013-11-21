package com.urd.triple.core.commands;

public class LoginReq extends Command {
    public static final int ID = 1;

    public String name;

    public LoginReq(String name) {
        this.name = name;
    }
}
