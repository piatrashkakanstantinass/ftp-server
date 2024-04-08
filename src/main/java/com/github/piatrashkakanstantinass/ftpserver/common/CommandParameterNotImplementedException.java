package com.github.piatrashkakanstantinass.ftpserver.common;

public class CommandParameterNotImplementedException extends CommandProcessingException {
    public CommandParameterNotImplementedException() {
        super("Command not implemented for that parameter", ReplyType.COMMAND_PARAMETER_NOT_IMPLEMENTED);
    }
}
