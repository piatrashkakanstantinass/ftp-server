package com.github.piatrashkakanstantinass.ftpserver;

import com.github.piatrashkakanstantinass.ftpserver.datatype.DataTypeHandler;
import com.github.piatrashkakanstantinass.ftpserver.datatype.handlers.AsciiNonPrintDataTypeHandler;

import java.net.Socket;

public class DataTransferHandler {
    private DataTypeHandler dataTypeHandler = new AsciiNonPrintDataTypeHandler();
    private Socket connSocket;

    public DataTypeHandler getDataTypeHandler() {
        return dataTypeHandler;
    }

    public void setDataTypeHandler(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
    }

    
}
