package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    private static final int PORT = 21;

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("shutting down")));
        if (args.length != 1) {
            System.err.println("Usage: ftpserver PATH");
            System.exit(1);
        }
        var server = new FTPServer(Path.of(args[0]));
        server.listen(PORT);
    }
}
