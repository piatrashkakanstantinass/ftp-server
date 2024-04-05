package com.github.piatrashkakanstantinass.ftpserver.common;

public class CommandParameterSyntaxErrorException extends CommandProcessingException {
    public CommandParameterSyntaxErrorException() {
        super("Syntax error in parameters", ReplyType.PARAMETER_SYNTAX_ERROR);
    }
}
