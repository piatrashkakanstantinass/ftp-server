package com.github.piatrashkakanstantinass.ftpserver;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private final Map<String, Command> commands = new HashMap<>();

    public static String getArg(String command) {
        var i = getCommandNameEndIndex(command);
        try {
            return command.substring(i + 1);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static int getCommandNameEndIndex(String command) {
        var i = command.indexOf(' ');
        if (i == -1) return command.length();
        return i;
    }

    public void addCommand(String name, Command cmd) {
        commands.put(name, cmd);
    }

    public void addCommand(String name, RequiredStringArgCommand cmd) {
        commands.put(name, cmd);
    }

    public void addCommand(String name, NoArgCommand cmd) {
        commands.put(name, cmd);
    }

    public Command getCommand(String command) {
        return commands.get(command.substring(0, getCommandNameEndIndex(command)).toLowerCase());
    }
}
