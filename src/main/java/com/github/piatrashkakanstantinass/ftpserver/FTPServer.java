package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.filesystem.LocalFileSystemProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;

public class FTPServer {
    private final Path path;

    public FTPServer(Path path) {
        this.path = path;
    }

    public void listen(int port) throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            while (!serverSocket.isClosed()) {
                var socket = serverSocket.accept();
                var session = new FTPSession(socket, new LocalFileSystemProvider(path));
                new Thread(session).start();
            }
        }
    }
}
