package com.github.piatrashkakanstantinass.ftpserver.filesystem;

import com.github.piatrashkakanstantinass.ftpserver.DataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

public class LocalFileSystem implements FileSystem {
    private final Path root;
    private Path currPath;
    private DataType dataType = DataType.ASCII_NON_PRINT;

    @Override
    public String pwd() {
        return "/" + root.relativize(currPath);
    }

    @Override
    public void setDataType(@NotNull DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public List<String> listFiles(@Nullable String path) throws IOException {
        var resolvedPath = currPath.resolve(path == null ? "" : path);
        var file = resolvedPath.toFile();
        if (!file.isDirectory()) {
            throw new NotDirectoryException(path);
        }
        try (var stream = Files.list(file.toPath())) {
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

    public LocalFileSystem(Path root) {
        this.root = root;
        this.currPath = root;
    }
}
