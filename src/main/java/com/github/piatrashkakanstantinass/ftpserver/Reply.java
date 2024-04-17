package com.github.piatrashkakanstantinass.ftpserver;

public enum Reply {
    FILE_STATUS_OKAY(150, "file status okay; about to open data connection"),
    COMMAND_OKAY(200, "command okay"),
    SERVICE_READY(220, "service ready for new user"),
    CLOSING_CONTROL_CONNECTION(221, "goodbye"),
    CLOSING_DATA_CONNECTION(226, "closing data connection"),
    ENTERING_PASSIVE_MODE(229),
    USER_LOGGED_IN(230, "user logged in, proceed"),
    REQUESTED_FILE_ACTION_OKAY(250, "requested file action okay"),
    PATHNAME_CREATED(257),
    CANT_OPEN_DATA_CONNECTION(425, "can't open data connection"),
    CONNECTION_CLOSED_TRANSFER_ABORTED(426, "data connection error"),
    FILE_ACTION_NOT_TAKEN(450, "file action not taken"),
    SYNTAX_ERROR_COMMAND_UNRECOGNIZED(500, "execution failed"),
    SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS(501, "syntax error in parameters or arguments"),
    REQUESTED_ACTION_NOT_TAKEN(550, "requested action not taken");

    private final int code;
    private String message;

    Reply(int code) {
        this.code = code;
    }

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
