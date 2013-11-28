package com.urd.triple.core.commands;

public class CardActionNotify extends Command {
    public static final int ID = 11;

    public CardAction action;

    public CardActionNotify(CardActionReq req) {
        src = req.src;
        action = new CardAction(req.action);
    }
}
