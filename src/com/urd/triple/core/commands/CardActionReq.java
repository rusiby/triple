package com.urd.triple.core.commands;

public class CardActionReq extends Command {
    public static final int ID = 10;

    public int card;
    public int srcArea;
    public int dstArea;

    public CardActionReq() {
    }
}
