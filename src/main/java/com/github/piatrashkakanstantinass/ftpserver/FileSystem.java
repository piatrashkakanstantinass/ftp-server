package com.github.piatrashkakanstantinass.ftpserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileSystem {
    private final File root;
    private File currentDirectory;

    public FileSystem(Path rootPath) {
        root = new File(rootPath.toString());
        currentDirectory = this.root;
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

    private static String formatFile(File f) throws IOException {
        var file = f.toPath();
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

    public String pwd() {
        return Paths.get("/", root.toPath().relativize(currentDirectory.toPath()).toString()).toString();
    }

    public List<String> list(String optPath) throws IOException {
        var dir = getFile(optPath);
        if (!dir.isDirectory() || !dir.exists()) {
            throw new IOException(String.format("%s is not a directory", optPath));
        }
        var files = dir.listFiles();
        if (files == null) {
            throw new IOException();
        }
        var fileStrs = new ArrayList<String>();
        for (var file : files) {
            fileStrs.add(formatFile(file));
        }
        return fileStrs;
    }

    public void cwd(String path) throws IOException {
        var newFile = getFile(path);
        if (!newFile.isDirectory()) throw new IOException(String.format("%s is not a directory", path));
        currentDirectory = newFile;
        System.out.printf("%s is a directory%n", currentDirectory);
    }

    public InputStream retr(String path) throws IOException {
        var file = getFile(path);
        if (!file.isFile()) throw new IOException();
        return new FileInputStream(file);
    }

    public void stor(String path, InputStream inputStream) throws IOException {
        var file = getAnyFile(path);
        file.createNewFile();
        var outputStream = new FileOutputStream(file);
        inputStream.transferTo(outputStream);
        outputStream.close();
    }

    public void dele(String path) throws IOException {
        var file = getFile(path);
        if (file.isDirectory() || !file.delete()) throw new IOException();
    }

    public void rmd(String path) throws IOException {
        var file = getFile(path);
        if (!file.isDirectory() || !file.delete()) throw new IOException();
    }

    public void mkd(String path) throws IOException {
        var file = getAnyFile(path);
        file.mkdirs();
    }

    private File getFile(String path) throws IOException {
        if (path == null) return currentDirectory;
        var newFile = new File(Paths.get(path).isAbsolute() ? root : currentDirectory, path);
        if (!newFile.exists()) {
            throw new IOException();
        }
        var normalizedPath = newFile.toPath().normalize();
        if (normalizedPath.toString().length() < root.toPath().normalize().toString().length()) {
            throw new IOException();
        }
        return new File(normalizedPath.toString()).getAbsoluteFile();
    }

    private File getAnyFile(String path) throws IOException {
        if (path == null) return currentDirectory;
        var newFile = new File(Paths.get(path).isAbsolute() ? root : currentDirectory, path);
        var normalizedPath = newFile.toPath().normalize();
        if (normalizedPath.toString().length() < root.toPath().normalize().toString().length()) {
            throw new IOException();
        }
        return new File(normalizedPath.toString()).getAbsoluteFile();
    }
}
