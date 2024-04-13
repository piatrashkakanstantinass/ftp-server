package com.github.piatrashkakanstantinass.ftpservercli;

import com.github.piatrashkakanstantinass.ftpserver.FTPServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static final int PORT = 21;
    private static final Path PATH = Paths.get("/Users/kanstantinaspiatrashka/test-files");

    public static void main(String[] args) throws IOException {
        try (var server = new FTPServer(PORT, PATH)) {
            server.listen();
        }
    }
}
