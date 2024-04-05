package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileSystemProvider implements FileSystemAccessProvider {
    private final Path rootPath;
    private Path relativePath = Paths.get("/");

    @Override
    public String getWorkingDirectory() {
        return relativePath.toString();
    }

    public LocalFileSystemProvider(Path path) {
        this.rootPath = path;
    }
}
