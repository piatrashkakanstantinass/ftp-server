package com.github.piatrashkakanstantinass.ftpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FTPSession implements Runnable {
    private final Socket socket;
    private static final Logger logger = Logger.getLogger(FTPSession.class.getName());

    public FTPSession(Socket socket) {
        this.socket = socket;
    }

    private String getSocketString() {
        return String.format("%s:%d", socket.getInetAddress().getHostAddress(), socket.getPort());
    }

    private String genSocketStringLogMessage(String msg) {
        return String.format("%s %s", this.getSocketString(), msg);
    }

    public void run() {
        logger.info(this.genSocketStringLogMessage("initiated a new session"));
        try {
            var inputStream = socket.getInputStream();
            var reader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                var line = reader.readLine();
                if (line == null) break;
                logger.info(this.genSocketStringLogMessage(String.format("Got message: %s", line)));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            logger.info(this.genSocketStringLogMessage("closed session"));
        }
    }
}
