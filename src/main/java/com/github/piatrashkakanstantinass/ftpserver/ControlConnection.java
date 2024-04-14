package com.github.piatrashkakanstantinass.ftpserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ControlConnection implements Closeable {
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public ControlConnection(Socket socket) throws IOException {
        this.socket = socket;
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
        bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.US_ASCII));
    }

    String read() throws IOException {
        return bufferedReader.readLine();
    }

    void write(ReplyCode replyCode) throws IOException {
        write(replyCode, replyCode.getMessage());
    }

    void write(ReplyCode replyCode, String message) throws IOException {
        bufferedWriter.write(String.format("%d %s\r\n", replyCode.getCode(), message));
        bufferedWriter.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
