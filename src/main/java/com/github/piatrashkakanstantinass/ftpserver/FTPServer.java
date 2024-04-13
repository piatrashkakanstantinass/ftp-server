package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.filesystem.LocalFileSystem;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FTPServer implements Closeable {
    private final ServerSocket serverSocket;
    private final List<FTPSession> sessions = Collections.synchronizedList(new ArrayList<>());
    private final Path path;

    public FTPServer(int port, Path path) throws IOException {
        serverSocket = new ServerSocket(port);
        this.path = path;
    }

    public void listen() throws IOException {
        while (!serverSocket.isClosed()) {
            var socket = serverSocket.accept();
            var session = new FTPSession(socket, new LocalFileSystem(path));
            session.startSession();
            synchronized (sessions) {
                sessions.add(session);
            }
        }
    }

    @Override
    public void close() throws IOException {
        IOException resultException = null;
        try {
            serverSocket.close();
        } catch (IOException e) {
            resultException = e;
        }
        synchronized (sessions) {
            for (var session : sessions) {
                try {
                    session.close();
                } catch (IOException e) {
                    resultException = e;
                }
            }
            sessions.clear();
        }
        if (resultException != null) {
            throw resultException;
        }
    }
}
