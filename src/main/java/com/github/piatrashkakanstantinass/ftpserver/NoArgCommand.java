package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public interface NoArgCommand extends Command {
    void run() throws IOException;

    @Override
    default void run(String arg) throws ReplyCodeException, IOException {
        if (arg != null) throw new ReplyCodeException(ReplyCode.PARAMETER_SYNTAX_ERROR);
        run();
    }
}
