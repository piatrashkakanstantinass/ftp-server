package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.datatype.DataTypeHandler;
import com.github.piatrashkakanstantinass.ftpserver.datatype.handlers.AsciiNonPrintDataTypeHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

public class DataTransferHandler {
    private DataTypeHandler dataTypeHandler = new AsciiNonPrintDataTypeHandler();
    private Socket connSocket;
    private InetAddress clientAddress;
    private int clientPort;
    private static final int BUFFER_SIZE = 4096;

    public DataTypeHandler getDataTypeHandler() {
        return dataTypeHandler;
    }

    public void setDataTypeHandler(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
    }

    public void setClientAddress(InetAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    private void initiateConnection() throws IOException {
        if (connSocket != null && !connSocket.isClosed()) {
            connSocket.close();
        }
        connSocket = new Socket(clientAddress, clientPort);
    }

    // TODO: only stream mode is implemented, abstract stuff later
    // File structure is assumed currently.
    public void send(InputStream inputStream) throws IOException {
        if (connSocket == null || connSocket.isClosed()) initiateConnection();
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true) {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead == -1) break;
            connSocket.getOutputStream().write(buffer, 0, bytesRead);
        }
        connSocket.close();
    }
}
