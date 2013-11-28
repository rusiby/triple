package com.urd.triple.core.commands;

import android.util.Log;

public class CardActionNotify extends Command {
    public static final int ID = 11;

    public CardAction action;

    public CardActionNotify(CardAction action) {
        this.action = new CardAction(action);
    }

    public CardActionNotify(CardActionReq req) {
        src = req.src;
        action = new CardAction(req.action);
    }

    @Override
    public CardActionNotify clone() throws CloneNotSupportedException {
        CardActionNotify command = new CardActionNotify(action);
        command.src = src;
        command.dst__ = dst__;

        return command;
    }
}
