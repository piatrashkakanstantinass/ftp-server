package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import com.github.piatrashkakanstantinass.ftpserver.DataType;
import org.jetbrains.annotations.NotNull;

public interface FileSystem {
    String pwd();

    void setDataType(@NotNull DataType dataType);
}
