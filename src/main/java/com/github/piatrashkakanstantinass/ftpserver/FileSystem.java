package com.github.piatrashkakanstantinass.ftpserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileSystem {
    private final Path root;
    private Path currentDirectory = Paths.get("/");

    public FileSystem(Path root) {
        this.root = root;
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private static String toListTimestamp(long time) {
        Date date = new Date(time);
        long sixMonths = 183L * 24L * 60L * 60L * 1000L;
        SimpleDateFormat yearFormat = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
        SimpleDateFormat hourFormat = new SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH);

        if (System.currentTimeMillis() - time > sixMonths) {
            return yearFormat.format(date);
        }
        return hourFormat.format(date);
    }

    private static String formatFile(Path file) throws IOException {
        var f = new File(file.toString());
        var dirIndicator = Files.isDirectory(file) ? "d" : "-";
        Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file);
        String permissionString = PosixFilePermissions.toString(permissions);
        long size = Files.size(file);
        String owner = Files.getOwner(file).getName();
        String group = Files.readAttributes(file, PosixFileAttributes.class).group().getName();
        String lastModified = toListTimestamp(f.lastModified());
        String filename = file.getFileName().toString();
        return String.format("%s%s   1 %s   %s   %d %s %s", dirIndicator, permissionString, owner, group, size, lastModified, filename);
    }

    public List<String> list(String pathname) throws IOException {
        var path = getPath(pathname);
        var values = new ArrayList<String>();
        if (!Files.isDirectory(path)) {
            values.add(formatFile(path));
            return values;
        }
        try (var stream = Files.newDirectoryStream(path)) {
            for (var file : stream) {
                values.add(formatFile(file));
            }
        }
        return values;
    }

    public boolean cwd(String pathname) {
        var path = getPath(pathname);
        if (!Files.isDirectory(path)) return true;
        currentDirectory = Paths.get("/", root.relativize(path).toString());
        return false;
    }

    public InputStream retr(String pathname) throws IOException {
        var path = getPath(pathname);
        return Files.newInputStream(path);
    }

    public OutputStream stor(String pathname) throws IOException {
        var path = getPath(pathname);
        return Files.newOutputStream(path);
    }

    public void dele(String pathname) throws IOException {
        var path = getPath(pathname);
        if (Files.isDirectory(path)) throw new IOException();
        Files.delete(path);
    }

    public void rmd(String pathname) throws IOException {
        var path = getPath(pathname);
        if (!Files.isDirectory(path)) throw new IOException();
        if (!deleteDirectory(new File(path.toString()))) throw new IOException();
    }

    public void mkd(String pathname) throws IOException {
        var path = getPath(pathname);
        Files.createDirectory(path);
    }

    private Path getPath(String pathname) {
        if (pathname == null) pathname = "";
        var path = currentDirectory.resolve(pathname).normalize();
        return Paths.get(root.toString(), path.toString());
    }

    public String pwd() {
        return currentDirectory.toString();
    }

}
