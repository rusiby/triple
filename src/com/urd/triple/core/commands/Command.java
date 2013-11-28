package com.urd.triple.core.commands;

import java.lang.reflect.Field;
import java.util.Locale;

import com.google.gson.Gson;

public class Command {

    public String src;
    public String dst__;

    public int getID() {
        int commandID = -1;

        try {
            Field field = getClass().getDeclaredField("ID");
            commandID = field.getInt(this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return commandID;
    }

    @Override
    public Command clone() throws CloneNotSupportedException {
        Command command = new Command();
        command.src = src;
        command.dst__ = dst__;

        return command;
    }

    public String toString() {
        Gson gson = new Gson();

        return String.format(Locale.US,
                "[%s id=%d data=%s]",
                getClass().getSimpleName(),
                getID(),
                gson.toJson(this));
    }
}
