package com.github.piatrashkakanstantinass.ftpserver.common;

public enum ReplyType {
    COMMAND_OK(200),
    SERVICE_READY(220),
    USER_LOGGED_IN(230),
    PATHNAME(257),
    SYNTAX_ERROR(500),
    PARAMETER_SYNTAX_ERROR(501),
    COMMAND_NOT_IMPLEMENTED(502),
    COMMAND_PARAMETER_NOT_IMPLEMENTED(504);

    private final int value;

    ReplyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
