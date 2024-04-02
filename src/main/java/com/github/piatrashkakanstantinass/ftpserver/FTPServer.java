package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;
import java.net.ServerSocket;

public class FTPServer {
    public void listen(int port) throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                var session = new FTPSession(socket);
                new Thread(session).start();
            }
        }
    }
}
