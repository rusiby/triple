package com.urd.triple.core.commands;

import java.util.Collection;

import com.urd.triple.core.Player;

public class LoginResp extends Command {
    public static final int ID = 2;

    public int errorCode;
    public Collection<Player> players;

    public LoginResp(int errorCode, Collection<Player> players) {
        this.errorCode = errorCode;
        this.players = players;
    }
}
