package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public class Main {
    private static final int PORT = 21;

    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("shutting down")));
        var server = new FTPServer();
        server.listen(PORT);
    }
}
