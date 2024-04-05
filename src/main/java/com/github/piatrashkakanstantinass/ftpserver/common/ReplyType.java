package com.github.piatrashkakanstantinass.ftpserver.common;

public enum ReplyType {
    SERVICE_READY(220),
    USER_LOGGED_IN(230),
    SYNTAX_ERROR(500),
    PARAMETER_SYNTAX_ERROR(501),
    COMMAND_NOT_IMPLEMENTED(502);

    private final int value;

    ReplyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
