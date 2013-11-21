package com.urd.triple.core.commands;

import com.urd.triple.core.Player;

public class LoginNotify extends Command {
    public static final int ID = 3;

    public Player player;

    public LoginNotify(Player player) {
        this.player = player;
    }
}
