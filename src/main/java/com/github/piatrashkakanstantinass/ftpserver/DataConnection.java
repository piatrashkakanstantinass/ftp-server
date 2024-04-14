package com.github.piatrashkakanstantinass.ftpserver;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class DataConnection implements Closeable {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public DataConnection(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public InputStream read() throws IOException {
        return inputStream;
    }

    public void write(InputStream inputStream) throws IOException {
        inputStream.transferTo(outputStream);
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
