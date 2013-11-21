package com.urd.triple.core.commands;

public class GameActionNotify extends Command {
    public static final int ID = 3;

    public enum OP_TYPE {
        OP_GET,
        OP_PUT
    }

    public int card;
    public OP_TYPE operation;
    public String srcID;
    public String dstID;

    public GameActionNotify() {
    }
}
