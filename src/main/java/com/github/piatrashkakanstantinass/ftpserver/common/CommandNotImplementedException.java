package com.github.piatrashkakanstantinass.ftpserver.common;

public class CommandNotImplementedException extends CommandProcessingException {
    public CommandNotImplementedException() {
        super("Command not implemented", ReplyType.COMMAND_NOT_IMPLEMENTED);
    }
}
