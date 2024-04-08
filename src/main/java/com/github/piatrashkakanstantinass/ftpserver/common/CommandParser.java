package com.github.piatrashkakanstantinass.ftpserver.common;

import com.github.piatrashkakanstantinass.ftpserver.commands.PortCommand;
import com.github.piatrashkakanstantinass.ftpserver.commands.PrintWorkingDirectoryCommand;
import com.github.piatrashkakanstantinass.ftpserver.commands.TypeCommand;
import com.github.piatrashkakanstantinass.ftpserver.commands.UserCommand;
import com.github.piatrashkakanstantinass.ftpserver.datatype.DataType;
import com.github.piatrashkakanstantinass.ftpserver.datatype.handlers.AsciiNonPrintDataTypeHandler;
import com.github.piatrashkakanstantinass.ftpserver.datatype.handlers.ImageDataTypeHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class CommandParser {
    public static Command parse(String line) throws CommandProcessingException {
        var items = line.split(" ");
        if (items.length == 0) throw new CommandSyntaxErrorException();
        return switch (items[0]) {
            case "EPRT" -> {
                if (items.length != 2 || items[1].length() < 1) throw new CommandParameterSyntaxErrorException();
                var delimeter = items[1].charAt(0);
                var eprtItems = items[1].split(Pattern.quote(Character.toString(delimeter)));
                if (eprtItems.length != 4) throw new CommandParameterSyntaxErrorException();
                try {
                    yield new PortCommand(InetAddress.getByName(eprtItems[2]), Integer.parseInt(eprtItems[3]));
                } catch (UnknownHostException | NumberFormatException e) {
                    throw new CommandParameterSyntaxErrorException();
                }
            }
            case "PWD" -> {
                if (items.length != 1) throw new CommandParameterSyntaxErrorException();
                yield new PrintWorkingDirectoryCommand();
            }
            case "TYPE" -> {
                if (items.length < 2) throw new CommandParameterSyntaxErrorException();
                switch (items[1]) {
                    case "A" -> {
                        if (items.length == 2 || (items.length == 3 && items[2] == "N"))
                            yield new TypeCommand(new AsciiNonPrintDataTypeHandler());
                    }
                    case "I" -> {
                        if (items.length != 2) throw new CommandParameterSyntaxErrorException();
                        yield new TypeCommand(new ImageDataTypeHandler());
                    }
                }
                throw new CommandParameterNotImplementedException();
            }
            case "USER" -> {
                if (items.length != 2) throw new CommandParameterSyntaxErrorException();
                yield new UserCommand(items[1]);
            }
            default -> throw new CommandNotImplementedException();
        };
    }
}
