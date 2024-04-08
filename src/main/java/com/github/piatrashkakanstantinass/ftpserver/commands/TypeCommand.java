package com.github.piatrashkakanstantinass.ftpserver.commands;

import com.github.piatrashkakanstantinass.ftpserver.common.Command;
import com.github.piatrashkakanstantinass.ftpserver.common.ReplyType;
import com.github.piatrashkakanstantinass.ftpserver.datatype.DataType;
import com.github.piatrashkakanstantinass.ftpserver.common.FTPSessionState;
import com.github.piatrashkakanstantinass.ftpserver.common.Reply;
import com.github.piatrashkakanstantinass.ftpserver.datatype.DataTypeHandler;

public class TypeCommand extends Command {
    @Override
    public Reply process(FTPSessionState state) {
        state.getDataTransferHandler().setDataTypeHandler(dataTypeHandler);
        return new Reply("Command OK", ReplyType.COMMAND_OK);
    }

    private DataTypeHandler dataTypeHandler;

    public TypeCommand(DataTypeHandler dataTypeHandler) {
        this.dataTypeHandler = dataTypeHandler;
    }
}
