package com.github.piatrashkakanstantinass.ftpserver.common;

import com.github.piatrashkakanstantinass.ftpserver.commands.PrintWorkingDirectoryCommand;
import com.github.piatrashkakanstantinass.ftpserver.commands.UserCommand;

public class CommandParser {
    public static Command parse(String line) throws CommandProcessingException {
        var items = line.split(" ");
        if (items.length == 0) throw new CommandSyntaxErrorException();
        return switch (items[0]) {
            case "USER" -> {
                if (items.length != 2) throw new CommandParameterSyntaxErrorException();
                yield new UserCommand(items[1]);
            }
            case "PWD" -> {
                if (items.length != 1) throw new CommandParameterSyntaxErrorException();
                yield new PrintWorkingDirectoryCommand();
            }
            default -> throw new CommandNotImplementedException();
        };
    }
}
