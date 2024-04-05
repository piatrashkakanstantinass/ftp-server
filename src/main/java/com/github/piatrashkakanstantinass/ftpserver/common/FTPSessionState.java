package com.github.piatrashkakanstantinass.ftpserver.common;

import com.github.piatrashkakanstantinass.ftpserver.filesystem.FileSystemAccessProvider;

public class FTPSessionState {
    public FTPSessionState(FileSystemAccessProvider fileSystemAccessProvider) {
        this.fileSystemAccessProvider = fileSystemAccessProvider;
    }

    public FileSystemAccessProvider getFileSystemAccessProvider() {
        return fileSystemAccessProvider;
    }

    private final FileSystemAccessProvider fileSystemAccessProvider;
}
