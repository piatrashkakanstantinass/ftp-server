package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import com.github.piatrashkakanstantinass.ftpserver.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

public class LocalFileSystem implements FileSystem {
    private final File root;
    private File currFile;
    private DataType dataType = DataType.ASCII_NON_PRINT;

    @Override
    public String pwd() throws IOException {
        var rootPath = Paths.get(root.getAbsolutePath());
        return "/" + rootPath.relativize(Paths.get(getFile(".").getAbsolutePath())).toString();
    }

    @Override
    public void cwd(@NotNull String path) throws IOException {
        var file = getFile(path);
        currFile = file;
    }

    @Override
    public void rmd(@NotNull String path) throws IOException {
        var file = getFile(path);
        if (file.getAbsolutePath() == root.getAbsolutePath() || !file.isDirectory()) {
            throw new IOException();
        }
        file.delete();
    }

    @Override
    public void setDataType(@NotNull DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public List<String> listFiles(@Nullable String path) throws IOException {
        try (var stream = Files.list(getFile(path).toPath())) {
            return stream.map(s -> {
                try {
                    return formatFile(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } catch (RuntimeException e) {
            throw new IOException(e);
        }
    }

    private File getFile(String path) throws IOException {
        if (path == null) {
            path = ".";
        }
        var actualPath = Paths.get(path);
        File file;
        if (actualPath.isAbsolute()) {
            file = new File(Paths.get(root.getAbsolutePath().toString(), path).toString()).getAbsoluteFile();
        } else {
            file = new File(currFile, path).getAbsoluteFile();
        }
        if (file.getAbsolutePath().length() < root.getAbsolutePath().length()) {
            throw new IOException(); // prohibited
        }
        System.out.println(file.getAbsoluteFile());
        return file;
    }

    private String formatFile(Path file) throws IOException {
        var dirIndicator = Files.isDirectory(file) ? "d" : "-";
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file);
        String permissionString = PosixFilePermissions.toString(permissions);
        long size = Files.size(file);
        String owner = Files.getOwner(file).getName();
        String group = Files.readAttributes(file, PosixFileAttributes.class).group().getName();
        String lastModified = Files.getLastModifiedTime(file).toString();
        String filename = file.getFileName().toString();
        return String.format("%s%s   1 %s   %s   %d %s %s", dirIndicator, permissionString, owner, group, size, lastModified, filename);
    }

    public LocalFileSystem(@NotNull Path root) {
        this.root = new File(root.toAbsolutePath().toString());
        this.currFile = new File(root.toAbsolutePath().toString());
    }
}
