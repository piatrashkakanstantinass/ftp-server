package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.filesystem.FileSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FTPSession implements Closeable {
    private static final Logger logger = Logger.getLogger(FTPSession.class.getName());
    private final Socket socket;
    private final BufferedReader controlReader;
    private final BufferedWriter controlWriter;
    private final CommandHandler commandHandler = new CommandHandler(this);
    private final FileSystem fileSystem;
    private InetAddress hostDataAddress;
    private int hostDataPort;
    private Socket dataSocket = null;
    private final ReentrantLock dataConnectionLock = new ReentrantLock();

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

    public void sendData(@NotNull List<String> input) {
        try {
            dataConnectionLock.lock();
            var writer = new BufferedWriter(new OutputStreamWriter(dataSocket.getOutputStream()));
            input.forEach(inp -> {
                try {
                    System.out.println(inp);
                    writer.write(inp);
                    writer.write("\r\n");
                    writer.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            dataSocket.close();
        } catch (IOException | RuntimeException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } finally {
            dataSocket = null;
            dataConnectionLock.unlock();
        }
    }

    public @Nullable Socket openNewDataSocket() throws IOException, DataConnectionBusyException {
        var acquired = dataConnectionLock.tryLock();
        if (acquired) {
            try {
                if (dataSocket == null && hostDataAddress != null) {
                    dataSocket = new Socket(hostDataAddress, hostDataPort);
                    return dataSocket;
                }
                return null;
            } finally {
                dataConnectionLock.unlock();
            }
        }
        throw new DataConnectionBusyException();
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
