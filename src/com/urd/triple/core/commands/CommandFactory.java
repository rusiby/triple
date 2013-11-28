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
        COMMAND_MAP.append(StartGameReq.ID, StartGameReq.class);
        COMMAND_MAP.append(StartGameResp.ID, StartGameResp.class);
        COMMAND_MAP.append(StartGameNotify.ID, StartGameNotify.class);
        COMMAND_MAP.append(HeroListNotify.ID, HeroListNotify.class);
        COMMAND_MAP.append(SelectHeroNotify.ID, SelectHeroNotify.class);
        COMMAND_MAP.append(CardActionReq.ID, CardActionReq.class);
        COMMAND_MAP.append(CardActionNotify.ID, CardActionNotify.class);
        COMMAND_MAP.append(CleanDeskReq.ID, CleanDeskReq.class);
        COMMAND_MAP.append(CleanDeskNotify.ID, CleanDeskNotify.class);
        COMMAND_MAP.append(ChangeHPNotify.ID, ChangeHPNotify.class);
        COMMAND_MAP.append(RoleNotify.ID, RoleNotify.class);
    }
}
