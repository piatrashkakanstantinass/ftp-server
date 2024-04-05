package com.github.piatrashkakanstantinass.ftpserver.commands;

import com.github.piatrashkakanstantinass.ftpserver.common.Command;
import com.github.piatrashkakanstantinass.ftpserver.common.FTPSessionState;
import com.github.piatrashkakanstantinass.ftpserver.common.Reply;
import com.github.piatrashkakanstantinass.ftpserver.common.ReplyType;

public class UserCommand extends Command {
    private final String username;

    public UserCommand(String username) {
        this.username = username;
    }

    @Override
    public Reply process(FTPSessionState state) {
        return new Reply("User logged in", ReplyType.USER_LOGGED_IN);
    }
}
