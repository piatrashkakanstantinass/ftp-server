package com.github.piatrashkakanstantinass.ftpserver.common;

public abstract class Command {
    public abstract Reply process(FTPSessionState state);
}
