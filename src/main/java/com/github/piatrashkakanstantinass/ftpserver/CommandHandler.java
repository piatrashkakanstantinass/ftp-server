package com.github.piatrashkakanstantinass.ftpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;

public class CommandHandler {
    private final FtpSession ftpSession;
    private final FileSystem fileSystem;

    public CommandHandler(FtpSession ftpSession) {
        this.ftpSession = ftpSession;
        fileSystem = ftpSession.getFileSystem();
    }

    public void controlWrite(ReplyCode replyCode) throws IOException {
        ftpSession.getControlConnection().write(replyCode);
    }

    public void controlWrite(ReplyCode replyCode, String message) throws IOException {
        ftpSession.getControlConnection().write(replyCode, message);
    }


    public void user(RequiredStringArg username) throws IOException {
        controlWrite(ReplyCode.LOGGED_IN);
    }

    public void pwd() throws IOException {
        controlWrite(ReplyCode.PATHNAME, String.format("\"%s\" is pwd", fileSystem.pwd()));
    }

    public void type(RequiredStringArg typeCode) throws IOException {
        switch (typeCode.getArg().toLowerCase()) {
            case "i":
                ftpSession.setFileType(FileType.IMAGE);
                break;
            case "a":
            case "a n":
                ftpSession.setFileType(FileType.ASCII_NON_PRINT);
                break;
            default:
                controlWrite(ReplyCode.PARAMETER_NOT_IMPLEMENTED);
                return;
        }
        controlWrite(ReplyCode.OK);
    }

    public void eprt(RequiredStringArg arg) throws IOException {
        var str = arg.getArg();
        var delimiter = str.charAt(0);
        var args = str.split(Pattern.quote(Character.toString(delimiter)));
        try {
            ftpSession.setPort(InetAddress.getByName(args[2]), Integer.parseInt(args[3]));
        } catch (Exception e) {
            controlWrite(ReplyCode.PARAMETER_SYNTAX_ERROR);
            return;
        }
        controlWrite(ReplyCode.OK);
    }

    public void list(String optPath) throws IOException, ReplyCodeException {
        try {
            var files = fileSystem.list(optPath);
            writeDataConnection(files);
        } catch (IOException e) {
            controlWrite(ReplyCode.FILE_ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void cwd(RequiredStringArg dir) throws IOException {
        try {
            fileSystem.cwd(dir.getArg());
        } catch (IOException e) {
            controlWrite(ReplyCode.ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void retr(RequiredStringArg path) throws ReplyCodeException, IOException {
        try {
            var inputStream = fileSystem.retr(path.getArg());
            writeDataConnection(inputStream);
            inputStream.close();
        } catch (IOException e) {
            controlWrite(ReplyCode.ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void stor(RequiredStringArg path) throws ReplyCodeException, IOException {
        try {
            ftpSession.openDataConnection();
            var inputStream = ftpSession.readDataConnection();
            fileSystem.stor(path.getArg(), inputStream);
            ftpSession.closeDataConnection();
        } catch (IOException e) {
            throw new ReplyCodeException(ReplyCode.TRANSFER_ABORTED);
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void dele(RequiredStringArg path) throws IOException {
        try {
            fileSystem.dele(path.getArg());
        } catch (IOException e) {
            controlWrite(ReplyCode.ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void rmd(RequiredStringArg path) throws IOException {
        try {
            fileSystem.rmd(path.getArg());
        } catch (IOException e) {
            controlWrite(ReplyCode.ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    public void mkd(RequiredStringArg path) throws IOException {
        try {
            fileSystem.mkd(path.getArg());
        } catch (IOException e) {
            controlWrite(ReplyCode.ACTION_NOT_TAKEN);
            return;
        }
        controlWrite(ReplyCode.FILE_ACTION_OK);
    }

    private void openDataConnection() throws ReplyCodeException {
        try {
            ftpSession.openDataConnection();
        } catch (IOException e) {
            throw new ReplyCodeException(ReplyCode.FAILED_TO_OPEN_DATA);
        }
    }

    private void writeDataConnection(List<String> input) throws ReplyCodeException {
        var outputBuilder = new StringBuilder();
        for (var value : input) {
            outputBuilder.append(value);
            outputBuilder.append("\r\n");
        }
        var bytes = outputBuilder.toString().getBytes(StandardCharsets.US_ASCII);
        writeDataConnection(new ByteArrayInputStream(bytes));
    }

    private void writeDataConnection(InputStream inputStream) throws ReplyCodeException {
        openDataConnection();
        try {
            ftpSession.writeDataConnection(inputStream);
        } catch (IOException e) {
            throw new ReplyCodeException(ReplyCode.TRANSFER_ABORTED);
        }
    }
}
