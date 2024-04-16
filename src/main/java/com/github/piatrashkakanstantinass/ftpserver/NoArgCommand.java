package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public interface NoArgCommand extends Command {
    @Override
    default void execute(String arg, Session session) throws IOException {
        execute(session);
    }

    void execute(Session ignoredSession) throws IOException;
}
