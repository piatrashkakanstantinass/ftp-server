package com.github.piatrashkakanstantinass.ftpserver.common;

public class Reply {
    private final String message;
    private final ReplyType replyType;

    public ReplyType getReplyType() {
        return replyType;
    }

    public String getMessage() {
        return message;
    }

    public Reply(String message, ReplyType replyType) {
        this.message = message;
        this.replyType = replyType;
    }
}
