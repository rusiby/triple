package com.urd.triple.core.commands;

import android.util.SparseArray;

import com.google.gson.Gson;

public class CommandFactory {

    private static final SparseArray<Class<? extends Command>> COMMAND_MAP;

    public static Command getCommand(int commandID, String data) {
        Command command = null;

        Class<? extends Command> klass = COMMAND_MAP.get(commandID);
        if (klass != null) {
            Gson gson = new Gson();
            command = gson.fromJson(data, klass);
        }

        return command;
    }

    public static Class<? extends Command> getClass(int commandID) {
        return COMMAND_MAP.get(commandID);
    }

    static {
        COMMAND_MAP = new SparseArray<Class<? extends Command>>();
        COMMAND_MAP.append(LoginReq.ID, LoginReq.class);
        COMMAND_MAP.append(LoginResp.ID, LoginResp.class);
        COMMAND_MAP.append(LoginNotify.ID, LoginNotify.class);
        COMMAND_MAP.append(LogoutNotify.ID, LogoutNotify.class);
    }
}
