package com.github.piatrashkakanstantinass.ftpserver;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class CommandHandler {
    public void user(Session session) throws IOException {
        session.write(Reply.USER_LOGGED_IN);
    }

    public void type(String type, Session session) throws IOException {
        switch (type.toLowerCase()) {
            case "i":
                session.setAscii(false);
                break;
            case "a":
            case "a n":
                session.setAscii(true);
                break;
            default:
                session.write(Reply.SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS);
                System.out.println("TYPE " + type);
                return;
        }
        session.write(Reply.COMMAND_OKAY);
    }

    public void cwd(String pathname, Session session) throws IOException {
        if (session.getFileSystem().cwd(pathname)) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
        } else {
            session.write(Reply.REQUESTED_FILE_ACTION_OKAY);
        }
    }

    public void cdup(Session session) throws IOException {
        if (session.getFileSystem().cwd("..")) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
        } else {
            session.write(Reply.COMMAND_OKAY);
        }
    }

    public void pwd(Session session) throws IOException {
        session.write(Reply.PATHNAME_CREATED, String.format("\"%s\" is current directory", session.getFileSystem().pwd()));
    }

    public void list(String optPathname, Session session) throws IOException {
        List<String> files;
        try {
            files = session.getFileSystem().list(optPathname, true);
        } catch (IOException e) {
            session.write(Reply.FILE_ACTION_NOT_TAKEN);
            return;
        }
        list(files, session);
    }

    public void nlist(String optPathname, Session session) throws IOException {
        List<String> files;
        try {
            files = session.getFileSystem().list(optPathname, false);
        } catch (IOException e) {
            session.write(Reply.FILE_ACTION_NOT_TAKEN);
            return;
        }
        list(files, session);
    }

    private void list(List<String> files, Session session) throws IOException {
        session.write(Reply.FILE_STATUS_OKAY);
        var dataSocket = getDataConnection(session);
        if (dataSocket == null) return;
        var output = new StringBuilder();
        for (var file : files) {
            output.append(file);
            output.append("\r\n");
        }
        try (dataSocket) {
            dataSocket.getOutputStream().write(output.toString().getBytes());
        } catch (IOException e) {
            session.write(Reply.CONNECTION_CLOSED_TRANSFER_ABORTED);
            return;
        }
        session.write(Reply.CLOSING_DATA_CONNECTION);
    }

    public void retr(String pathname, Session session) throws IOException {
        InputStream inputStream;
        try {
            inputStream = session.getFileSystem().retr(pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
            return;
        }
        session.write(Reply.FILE_STATUS_OKAY);
        var dataSocket = getDataConnection(session);
        if (dataSocket == null) return;
        try (dataSocket) {
            if (session.isAscii()) {
                var reader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    var line = reader.readLine();
                    if (line == null) break;
                    dataSocket.getOutputStream().write(line.getBytes());
                    dataSocket.getOutputStream().write("\r\n".getBytes());
                }
            } else {
                inputStream.transferTo(dataSocket.getOutputStream());
            }
        } catch (IOException e) {
            session.write(Reply.CONNECTION_CLOSED_TRANSFER_ABORTED);
            return;
        } finally {
            if (inputStream != null) inputStream.close();
        }
        session.write(Reply.CLOSING_DATA_CONNECTION);
    }

    public void stor(String pathname, Session session) throws IOException {
        OutputStream outputStream;
        try {
            outputStream = session.getFileSystem().stor(pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
            return;
        }
        session.write(Reply.FILE_STATUS_OKAY);
        var dataSocket = getDataConnection(session);
        if (dataSocket == null) return;
        try (dataSocket) {
            if (session.isAscii()) {
                var reader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
                var writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                while (true) {
                    var line = reader.readLine();
                    if (line == null) break;
                    writer.write(line);
                    writer.newLine();
                    writer.flush();
                }
            } else {
                dataSocket.getInputStream().transferTo(outputStream);
            }
        } catch (IOException e) {
            session.write(Reply.CONNECTION_CLOSED_TRANSFER_ABORTED);
            return;
        } finally {
            if (outputStream != null) outputStream.close();
        }
        session.write(Reply.CLOSING_DATA_CONNECTION);
    }

    public void dele(String pathname, Session session) throws IOException {
        try {
            session.getFileSystem().dele(pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
            return;
        }
        session.write(Reply.REQUESTED_FILE_ACTION_OKAY);
    }

    public void rmd(String pathname, Session session) throws IOException {
        try {
            session.getFileSystem().rmd(pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
            return;
        }
        session.write(Reply.REQUESTED_FILE_ACTION_OKAY);
    }

    public void mkd(String pathname, Session session) throws IOException {
        try {
            session.getFileSystem().mkd(pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN);
            return;
        }
        session.write(Reply.PATHNAME_CREATED, String.format("\"%s\" created", pathname));
    }

    public void quit(Session session) throws IOException {
        session.write(Reply.CLOSING_CONTROL_CONNECTION);
        session.close();
    }

    public void epsv(Session session) throws IOException {
        int port;
        try {
            port = session.openDataPassiveListener();
        } catch (IOException e) {
            session.write(Reply.SYNTAX_ERROR_COMMAND_UNRECOGNIZED);
            System.err.println(e.getMessage());
            return;
        }
        session.write(Reply.ENTERING_PASSIVE_MODE, String.format("entering passive mode (|||%d|)", port));
    }

    public void rnfr(String pathname, Session session) throws IOException {
        session.setRenameFrom(pathname);
        session.write(Reply.FILE_ACTION_PENDING_INFO);
    }

    public void rnto(String pathname, Session session) throws IOException {
        var from = session.getRenameFrom();
        if (from == null) {
            session.write(Reply.BAD_SEQUENCE);
            return;
        }
        try {
            session.getFileSystem().rename(from, pathname);
        } catch (IOException e) {
            session.write(Reply.REQUESTED_ACTION_NOT_TAKEN_NOT_ALLOWED);
            return;
        }
        session.write(Reply.REQUESTED_FILE_ACTION_OKAY);
    }

    private Socket getDataConnection(Session session) throws IOException {
        try {
            return session.getDataSocket();
        } catch (IOException | NullPointerException e) {
            session.write(Reply.CANT_OPEN_DATA_CONNECTION);
            return null;
        }
    }
}
