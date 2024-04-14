package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public interface Command {
    void run(String arg) throws ReplyCodeException, IOException;
}
