package com.github.piatrashkakanstantinass.ftpserver.commands;

import com.github.piatrashkakanstantinass.ftpserver.common.Command;
import com.github.piatrashkakanstantinass.ftpserver.common.FTPSessionState;
import com.github.piatrashkakanstantinass.ftpserver.common.Reply;
import com.github.piatrashkakanstantinass.ftpserver.common.ReplyType;

public class PrintWorkingDirectoryCommand extends Command {
    @Override
    public Reply process(FTPSessionState state) {
        var directory = state.getFileSystemAccessProvider().getWorkingDirectory();
        return new Reply(String.format("\"%s\" is the current directory", directory), ReplyType.PATHNAME);
    }
}
