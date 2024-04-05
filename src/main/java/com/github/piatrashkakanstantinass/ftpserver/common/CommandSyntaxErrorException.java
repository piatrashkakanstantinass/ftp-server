package com.github.piatrashkakanstantinass.ftpserver.common;

public class CommandSyntaxErrorException extends CommandProcessingException {
    public CommandSyntaxErrorException() {
        super("Syntax error", ReplyType.SYNTAX_ERROR);
    }
}
