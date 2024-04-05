package com.github.piatrashkakanstantinass.ftpserver.common;

public abstract class CommandProcessingException extends Exception {
    private final ReplyType replyType;

    public CommandProcessingException(String message, ReplyType replyType) {
        super(message);
        this.replyType = replyType;
    }

    public ReplyType getReply() {
        return replyType;
    }
}
