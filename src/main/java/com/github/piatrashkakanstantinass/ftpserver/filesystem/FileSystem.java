package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import com.github.piatrashkakanstantinass.ftpserver.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.stream.Stream;

public interface FileSystem {
    String pwd() throws IOException;

    void cwd(@NotNull String path) throws IOException;

    void rmd(@NotNull String path) throws IOException;

    void mkd(@NotNull String path) throws IOException;

    void setDataType(@NotNull DataType dataType);

    List<String> listFiles(@Nullable String path) throws IOException;
}
