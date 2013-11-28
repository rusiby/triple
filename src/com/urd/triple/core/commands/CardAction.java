package com.urd.triple.core.commands;

public class CardAction extends Command {

    public static final int MODE_GET = 0;
    public static final int MODE_PUT = 1;

    public int card;
    public int mode;
    public int srcArea;
    public int dstArea;
    public String target;

    public CardAction(int card, int mode, int srcArea, int dstArea, String target) {
        this.card = card;
        this.mode = mode;
        this.srcArea = srcArea;
        this.dstArea = dstArea;
        this.target = target;
    }

    public CardAction(CardAction action) {
        this.card = action.card;
        this.mode = action.mode;
        this.srcArea = action.srcArea;
        this.dstArea = action.dstArea;
        this.target = action.target;
    }
}
