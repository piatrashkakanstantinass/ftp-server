package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import com.github.piatrashkakanstantinass.ftpserver.DataType;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileSystem implements FileSystem {
    private final Path root;
    private Path relativePath = Paths.get("/");
    private DataType dataType = DataType.ASCII_NON_PRINT;

    @Override
    public String pwd() {
        return relativePath.toString();
    }

    @Override
    public void setDataType(@NotNull DataType dataType) {
        this.dataType = dataType;
    }

    public LocalFileSystem(Path root) {
        this.root = root;
    }
}
