package com.github.piatrashkakanstantinass.ftpserver.common;

import com.github.piatrashkakanstantinass.ftpserver.commands.UserCommand;

public class CommandParser {
    public static Command parse(String line) throws CommandProcessingException {
        var items = line.split(" ");
        if (items.length == 0) throw new CommandSyntaxErrorException();
        switch (items[0]) {
            case "USER":
                if (items.length != 2) throw new CommandParameterSyntaxErrorException();
                return new UserCommand(items[1]);
            default:
                throw new CommandNotImplementedException();
        }
    }
}
