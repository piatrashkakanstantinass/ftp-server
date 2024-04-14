package com.github.piatrashkakanstantinass.ftpserver;

import org.apache.commons.io.input.ReaderInputStream;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FtpSession implements Closeable {
    private final ControlConnection controlConnection;
    private final CommandParser commandParser = new CommandParser();
    private final FileSystem fileSystem;
    private com.github.piatrashkakanstantinass.ftpserver.CommandHandler commandHandler;
    private InetAddress inetAddress;
    private int port;
    private DataConnection dataConnection;
    private FileType fileType = FileType.ASCII_NON_PRINT;

    public FtpSession(Socket socket, Path path) throws IOException {
        this.controlConnection = new ControlConnection(socket);
        fileSystem = new FileSystem(path);
        commandHandler = new CommandHandler(this);
        commandParser.addCommand("user", commandHandler::user);
        commandParser.addCommand("pwd", commandHandler::pwd);
        commandParser.addCommand("type", commandHandler::type);
        commandParser.addCommand("eprt", commandHandler::eprt);
        commandParser.addCommand("list", commandHandler::list);
        commandParser.addCommand("cwd", commandHandler::cwd);
        commandParser.addCommand("retr", commandHandler::retr);
        commandParser.addCommand("stor", commandHandler::stor);
        commandParser.addCommand("dele", commandHandler::dele);
    }

    public ControlConnection getControlConnection() {
        return controlConnection;
    }

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public FileSystem getFileSystem() {
        return fileSystem;
    }

    public void setPort(InetAddress address, int port) {
        inetAddress = address;
        this.port = port;
    }

    public void openDataConnection() throws IOException {
        if (dataConnection != null && !dataConnection.isClosed()) return;
        try {
            dataConnection = new DataConnection(new Socket(inetAddress, port));
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public InputStream readDataConnection() throws IOException {
        return dataConnection.read();
    }

    public void writeDataConnection(InputStream inputStream) throws IOException {
        if (fileType == FileType.ASCII_NON_PRINT) {
            dataConnection.write(new ReaderInputStream(new InputStreamReader(inputStream, StandardCharsets.US_ASCII)));
        } else {
            dataConnection.write(inputStream);
        }
        dataConnection = null;
    }

    public void closeDataConnection() throws IOException {
        if (dataConnection != null || !dataConnection.isClosed()) dataConnection.close();
    }

    public DataConnection getDataConnection() {
        return dataConnection;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public void close() throws IOException {
        commandHandler = null;
        controlConnection.close();
    }
}
