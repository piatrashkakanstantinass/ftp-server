package com.github.piatrashkakanstantinass.ftpserver;

import java.io.IOException;

public interface RequiredStringArgCommand extends Command {
    @Override
    default void run(String arg) throws ReplyCodeException, IOException {
        if (arg == null || arg.isEmpty()) {
            throw new ReplyCodeException(ReplyCode.PARAMETER_SYNTAX_ERROR);
        }
        run(new RequiredStringArg(arg));
    }

    void run(RequiredStringArg arg) throws IOException, ReplyCodeException;
}
