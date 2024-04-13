package com.github.piatrashkakanstantinass.ftpserver;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.InetAddress;
import java.util.regex.Pattern;

public class CommandHandler {
    private final FTPSession ftpSession;

    public CommandHandler(FTPSession ftpSession) {
        this.ftpSession = ftpSession;
    }

    public void handleCommand(@NotNull String command) throws IOException {
        var firstSpace = command.indexOf(' ');
        boolean noSpace = false;
        if (firstSpace == -1) {
            noSpace = true;
            firstSpace = command.length();
        }
        var args = noSpace ? null : command.substring(firstSpace + 1);
        switch (command.substring(0, firstSpace).toLowerCase()) {
            case "user":
                user(args);
                break;
            case "pwd":
                pwd();
                break;
            case "type":
                type(args);
                break;
            case "eprt":
                eprt(args);
                break;
            default:
                ftpSession.sendControl(Reply.COMMAND_NOT_IMPLEMENTED);
                break;
        }
    }

    private void user(String username) throws IOException {
        if (username == null) {
            ftpSession.sendControl(Reply.COMMAND_PARAMETER_SYNTAX_ERROR);
            return;
        }
        ftpSession.sendControl(Reply.USER_LOGGED_IN);
    }

    private void pwd() throws IOException {
        ftpSession.sendControl(Reply.PATHNAME, String.format("\"%s\" is PWD", ftpSession.getFileSystem().pwd()));
    }

    private void type(String arg) throws IOException {
        if (arg == null || arg.isEmpty()) {
            ftpSession.sendControl(Reply.COMMAND_PARAMETER_SYNTAX_ERROR);
            return;
        }
        DataType dataType = null;
        arg = arg.toLowerCase();
        switch (arg.charAt(0)) {
            case 'i':
                dataType = DataType.IMAGE;
            case 'a':
                if (arg.length() == 1) {
                    dataType = DataType.ASCII_NON_PRINT;
                } else if (arg.length() == 3 && arg.charAt(1) == ' ') {
                    if (arg.charAt(2) == 'n') {
                        dataType = DataType.ASCII_NON_PRINT;
                    }
                } else {
                    ftpSession.sendControl(Reply.COMMAND_PARAMETER_SYNTAX_ERROR);
                    return;
                }
        }
        if (dataType == null) {
            ftpSession.sendControl(Reply.COMMAND_PARAMETER_NOT_IMPLEMENTED);
        } else {
            ftpSession.getFileSystem().setDataType(dataType);
            ftpSession.sendControl(Reply.COMMAND_OK);
        }
    }

    private void eprt(String arg) throws IOException {
        if (arg == null || arg.isEmpty()) {
            ftpSession.sendControl(Reply.COMMAND_PARAMETER_SYNTAX_ERROR);
            return;
        }
        var delimeter = arg.charAt(0);
        var args = arg.split(Pattern.quote(Character.toString(delimeter)));
        if (args.length < 4) {
            ftpSession.sendControl(Reply.COMMAND_PARAMETER_SYNTAX_ERROR);
            return;
        }
        ftpSession.setHostDataAddress(InetAddress.getByName(args[2]), Integer.parseInt(args[3]));
        ftpSession.sendControl(Reply.COMMAND_OK);
    }
}