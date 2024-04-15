package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String path;
        if (args.length < 1) {
            System.err.println("Usage: com.github.piatrashkakanstantinass.ftpserver path");
            System.exit(1);
            return;
        }
        path = args[0];
        try (var serverSocket = new ServerSocket(21)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        var ftpSession = new FtpSession(socket, Paths.get(path));
                        var controlConnection = ftpSession.getControlConnection();
                        var commandParser = ftpSession.getCommandParser();
                        controlConnection.write(ReplyCode.READY);
                        while (true) {
                            var commandStr = controlConnection.read();
                            if (commandStr == null) break;
                            if (!StandardCharsets.US_ASCII.newEncoder().canEncode(commandStr)) {
                                controlConnection.write(ReplyCode.UNRECOGNIZED); // When dealing with non ascii input
                                continue;
                            }
                            var cmd = commandParser.getCommand(commandStr);
                            var arg = CommandParser.getArg(commandStr);
                            if (cmd == null) {
                                controlConnection.write(ReplyCode.UNRECOGNIZED);
                                continue;
                            }
                            try {
                                cmd.run(arg);
                            } catch (ReplyCodeException e) {
                                controlConnection.write(e.getReplyCode(), e.getReplyMessage());
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        }
    }
}
