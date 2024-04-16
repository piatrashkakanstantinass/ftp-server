package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public interface Command {
    void execute(String ignoredArg, Session session) throws IOException;
}
