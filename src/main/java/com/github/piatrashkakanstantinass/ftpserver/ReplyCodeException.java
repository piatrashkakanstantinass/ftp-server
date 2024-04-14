package com.github.piatrashkakanstantinass.ftpserver;

public class ReplyCodeException extends Exception {
    private final ReplyCode replyCode;
    private final String replyMessage;

    public ReplyCodeException(ReplyCode replyCode) {
        this.replyCode = replyCode;
        replyMessage = replyCode.getMessage();
    }

    public ReplyCodeException(ReplyCode replyCode, String replyMessage) {
        this.replyCode = replyCode;
        this.replyMessage = replyMessage;
    }

    public ReplyCode getReplyCode() {
        return replyCode;
    }

    public String getReplyMessage() {
        return replyMessage;
    }
}
