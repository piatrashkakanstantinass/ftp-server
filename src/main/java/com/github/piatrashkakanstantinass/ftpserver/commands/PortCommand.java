package com.github.piatrashkakanstantinass.ftpserver.commands;

import com.github.piatrashkakanstantinass.ftpserver.common.Command;
import com.github.piatrashkakanstantinass.ftpserver.common.FTPSessionState;
import com.github.piatrashkakanstantinass.ftpserver.common.Reply;
import com.github.piatrashkakanstantinass.ftpserver.common.ReplyType;

import java.net.InetAddress;

public class PortCommand extends Command {
    private InetAddress host;
    private int port;

    public PortCommand(InetAddress host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Reply process(FTPSessionState state) {
        return new Reply("Ok", ReplyType.COMMAND_OK);
    }
}
