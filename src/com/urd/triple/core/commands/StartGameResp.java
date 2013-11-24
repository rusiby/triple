package com.urd.triple.core.commands;

public class StartGameResp extends Command {
    public static final int ID = 6;

    public static final int ERROR_INVALID_PLAYER_COUNT = 1;

    public int errorCode;

    public StartGameResp(int errorCode) {
        this.errorCode = errorCode;
    }
}
