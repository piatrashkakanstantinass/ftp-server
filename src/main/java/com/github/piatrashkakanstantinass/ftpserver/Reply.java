package com.github.piatrashkakanstantinass.ftpserver;

public enum Reply {
    ABOUT_TO_OPEN_DATA(150, "File status okay; about to open data connection"),
    COMMAND_OK(200, "Command OK"),
    READY(220, "Service ready for new user"),
    CLOSING_DATA_SUCCESS(226, "Closing data connection"),
    USER_LOGGED_IN(230, "User logged in, proceed"),
    PATHNAME(257, "Path name"),
    FAILED_TO_OPEN_DATA(425, "Can't open data connection"),
    COMMAND_SYNTAX_ERROR(500, "Syntax error, command unrecognized"),
    COMMAND_PARAMETER_SYNTAX_ERROR(501, "Syntax error in parameters"),
    COMMAND_NOT_IMPLEMENTED(502, "Command not implemented"),
    COMMAND_PARAMETER_NOT_IMPLEMENTED(504, "Command parameter not implemented"),
    ACTION_NOT_TAKEN(550, "Requested action not taken");

    private final int code;
    private final String message;

    Reply(int code, String message) {
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
