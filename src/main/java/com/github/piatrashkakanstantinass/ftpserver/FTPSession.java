package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.filesystem.FileSystem;
import com.github.piatrashkakanstantinass.ftpserver.filesystem.LocalFileSystem;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FTPSession implements Closeable {
    private static final Logger logger = Logger.getLogger(FTPSession.class.getName());
    private final Socket socket;
    private final BufferedReader controlReader;
    private final BufferedWriter controlWriter;
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final FileSystem fileSystem;
    private InetAddress hostDataAddress;
    private int hostDataPort;
    private final Thread controlThread = new Thread(() -> {
        logger.log(Level.INFO, "User connected");
        try {
            sendControl(Reply.READY);
            while (true) {
                var commandStr = receiveControl();
                logger.log(Level.INFO, String.format("Received command: %s", commandStr));
                commandHandler.handleCommand(commandStr);
            }
        } catch (IOException e) {
            if (!Thread.currentThread().isInterrupted()) {
                logger.log(Level.INFO, "User disconnected");
            }
        }
    });

    public FTPSession(@NotNull Socket socket, @NotNull FileSystem fileSystem) throws IOException {
        this.socket = socket;
        this.fileSystem = fileSystem;
        controlReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        controlWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void startSession() {
        controlThread.start();
    }

    public void sendControl(@NotNull Reply reply) throws IOException {
        sendControl(reply, reply.getMessage());
    }

    public void sendControl(@NotNull Reply reply, @NotNull String message) throws IOException {
        controlWriter.write(Integer.toString(reply.getCode()));
        controlWriter.write(" ");
        controlWriter.write(message);
        controlWriter.write("\r\n");
        controlWriter.flush();
    }

    private @NotNull String receiveControl() throws IOException {
        var line = controlReader.readLine();
        if (line == null) {
            throw new EOFException("Command expected");
        }
        return line;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public void close() throws IOException {
        controlThread.interrupt();
        socket.close();
    }

    public void setHostDataAddress(InetAddress hostDataAddress, int port) {
        this.hostDataAddress = hostDataAddress;
        this.hostDataPort = port;
    }
}
