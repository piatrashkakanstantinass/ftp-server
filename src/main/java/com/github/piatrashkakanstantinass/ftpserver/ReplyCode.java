package com.github.piatrashkakanstantinass.ftpserver;

public enum ReplyCode {
    OK(200, "ok"),
    READY(220, "ready"),
    LOGGED_IN(230, "logged in"),
    FILE_ACTION_OK(250, "file action ok"),
    PATHNAME(257),
    FAILED_TO_OPEN_DATA(425, "cannot open data"),
    TRANSFER_ABORTED(426, "transfer aborted"),
    FILE_ACTION_NOT_TAKEN(450, "file action not taken"),
    UNRECOGNIZED(500, "unrecognized"),
    PARAMETER_SYNTAX_ERROR(501, "parameter syntax error"),
    PARAMETER_NOT_IMPLEMENTED(504, "parameter not implemented"),
    ACTION_NOT_TAKEN(550, "action not taken"),
    ;

    private final int code;
    private final String message;

    ReplyCode(int code) {
        this.code = code;
        message = null;
    }

    ReplyCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
