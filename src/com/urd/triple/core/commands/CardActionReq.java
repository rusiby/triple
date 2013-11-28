package com.urd.triple.core.commands;

public class CardActionReq extends Command {
    public static final int ID = 10;

    public CardAction action;

    public CardActionReq(int card, int mode, int srcArea, int dstArea, String target) {
        action = new CardAction(card, mode, srcArea, dstArea, target);
    }
}
