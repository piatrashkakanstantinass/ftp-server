package com.github.piatrashkakanstantinass.ftpserver;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Session implements Closeable {
    private final Socket socket;
    private final FileSystem fileSystem;
    private ServerSocket passiveServerSocket;
    private boolean closed = false;
    private boolean ascii = true;
    private String renameFrom = null;

    public Session(Socket socket, FileSystem fileSystem) {
        this.socket = socket;
        this.fileSystem = fileSystem;
    }

    public String read() throws IOException {
        return new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
    }

    public void write(Reply reply) throws IOException {
        write(reply, reply.getMessage());
    }

    public void write(Reply reply, String message) throws IOException {
        socket.getOutputStream().write(String.format("%d %s\r\n", reply.getCode(), message).getBytes());
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public int openDataPassiveListener() throws IOException {
        var newSocket = new ServerSocket(0);
        if (passiveServerSocket != null) {
            passiveServerSocket.close();
        }
        passiveServerSocket = newSocket;
        return passiveServerSocket.getLocalPort();
    }

    public Socket getDataSocket() throws IOException {
        return passiveServerSocket.accept();
    }

    public boolean isClosed() {
        return closed;
    }

    public boolean isAscii() {
        return ascii;
    }

    public void setAscii(boolean ascii) {
        this.ascii = ascii;
    }

    public String getRenameFrom() {
        return renameFrom;
    }

    public void setRenameFrom(String renameFrom) {
        this.renameFrom = renameFrom;
    }

    @Override
    public void close() throws IOException {
        closed = true;
        if (passiveServerSocket != null) passiveServerSocket.close();
        socket.close();
    }
}
