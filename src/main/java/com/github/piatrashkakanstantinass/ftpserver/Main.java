package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

// TODO: ABORT
public class Main {
    private static void registerRequiredArgCommand(String commandStr, Command command, Map<String, CommandInfo> commands) {
        commands.put(commandStr, new CommandInfo(command, CommandArgumentRequirement.ARGUMENT_REQUIRED));
    }

    private static void registerNoArgCommand(String commandStr, NoArgCommand command, Map<String, CommandInfo> commands) {
        commands.put(commandStr, new CommandInfo(command, CommandArgumentRequirement.NO_ARGUMENT));
    }

    private static void registerCommands(CommandHandler commandHandler, Map<String, CommandInfo> commands) {
        commands.put("user", new CommandInfo(commandHandler::user, CommandArgumentRequirement.ARGUMENT_REQUIRED));
        commands.put("type", new CommandInfo(commandHandler::type, CommandArgumentRequirement.ARGUMENT_REQUIRED));
        registerRequiredArgCommand("cwd", commandHandler::cwd, commands);
        registerRequiredArgCommand("retr", commandHandler::retr, commands);
        registerRequiredArgCommand("stor", commandHandler::stor, commands);
        registerRequiredArgCommand("dele", commandHandler::dele, commands);
        registerRequiredArgCommand("rmd", commandHandler::rmd, commands);
        registerRequiredArgCommand("mkd", commandHandler::mkd, commands);
        registerRequiredArgCommand("rnfr", commandHandler::rnfr, commands);
        registerRequiredArgCommand("rnto", commandHandler::rnto, commands);
        registerNoArgCommand("cdup", commandHandler::cdup, commands);
        registerNoArgCommand("pwd", commandHandler::pwd, commands);
        commands.put("list", new CommandInfo(commandHandler::list, CommandArgumentRequirement.OPTIONAL_ARGUMENT));
        commands.put("nlst", new CommandInfo(commandHandler::nlist, CommandArgumentRequirement.OPTIONAL_ARGUMENT));
        registerNoArgCommand("quit", commandHandler::quit, commands);
        registerNoArgCommand("epsv", commandHandler::epsv, commands);
    }

    public static void main(String[] args) throws IOException {
        int port;
        Path path;
        try {
            port = Integer.parseInt(args[0]);
            path = Paths.get(args[1]);
        } catch (IndexOutOfBoundsException | NumberFormatException | InvalidPathException e) {
            System.err.println("usage: ftpserver port path");
            System.exit(1);
            return;
        }

        var commandHandler = new CommandHandler();
        var commands = new HashMap<String, CommandInfo>();
        registerCommands(commandHandler, commands);

        try (var serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var session = new Session(serverSocket.accept(), new FileSystem(path));
                new Thread(() -> {
                    try (session) {
                        session.write(Reply.SERVICE_READY);
                        while (!session.isClosed()) {
                            var commandStr = session.read();
                            if (commandStr == null) break;
                            var space = commandStr.indexOf(' ');
                            var commandName = commandStr.substring(0, space == -1 ? commandStr.length() : space).toLowerCase();
                            var commandInfo = commands.get(commandName);
                            if (commandInfo == null) {
                                session.write(Reply.SYNTAX_ERROR_COMMAND_UNRECOGNIZED);
                                System.out.println(commandStr);
                                continue;
                            }
                            var commandArg = space == -1 ? null : commandStr.substring(space + 1);
                            var wrongArg = false;
                            switch (commandInfo.argumentRequirement()) {
                                case ARGUMENT_REQUIRED:
                                    if (commandArg == null || commandArg.isEmpty()) wrongArg = true;
                                    break;
                                case NO_ARGUMENT:
                                    if (commandArg != null) wrongArg = true;
                                    break;
                            }
                            if (wrongArg) {
                                session.write(Reply.SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS);
                                continue;
                            }
                            commandInfo.command().execute(commandArg, session);
                        }
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                    }
                }).start();
            }
        }
    }
}
