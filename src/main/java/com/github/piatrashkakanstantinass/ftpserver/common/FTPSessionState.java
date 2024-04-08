package com.github.piatrashkakanstantinass.ftpserver.common;

import com.github.piatrashkakanstantinass.ftpserver.DataTransferHandler;
import com.github.piatrashkakanstantinass.ftpserver.filesystem.FileSystemAccessProvider;

public class FTPSessionState {
    private DataTransferHandler dataTransferHandler = new DataTransferHandler();
    private final FileSystemAccessProvider fileSystemAccessProvider;

    public DataTransferHandler getDataTransferHandler() {
        return dataTransferHandler;
    }

    public FTPSessionState(FileSystemAccessProvider fileSystemAccessProvider) {
        this.fileSystemAccessProvider = fileSystemAccessProvider;
    }

    public FileSystemAccessProvider getFileSystemAccessProvider() {
        return fileSystemAccessProvider;
    }
}
