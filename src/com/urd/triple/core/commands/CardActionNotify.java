package com.urd.triple.core.commands;

public class CardActionNotify extends Command {
    public static final int ID = 11;

    public int card;
    public int srcArea;
    public int dstArea;

    public CardActionNotify() {
    }
}