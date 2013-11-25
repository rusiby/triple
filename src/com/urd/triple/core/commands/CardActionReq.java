package com.urd.triple.core.commands;

public class CardActionReq extends Command {
    public static final int ID = 10;

    public int card;
    public int srcArea;
    public int dstArea;

    public CardActionReq(int card, int srcArea, int dstArea, String dst) {
        this.card = card;
        this.srcArea = srcArea;
        this.dstArea = dstArea;
        this.dst = dst;
    }
}
