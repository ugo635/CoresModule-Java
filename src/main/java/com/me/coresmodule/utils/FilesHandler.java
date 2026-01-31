package com.me.coresmodule.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FilesHandler {
    public static String configFolder = "config/coresmodule"; // Default folder is folder from where you call the function

    public static void register() throws IOException {
        createConfigFolder();
    }

    public static void createConfigFolder() throws IOException {
        if (!new File(configFolder).exists()) {
            File f = new File(configFolder);
            f.mkdir();
        }

    }

    public static void createNewFolder(String name) throws IOException {
        if (!new File(configFolder + '/' + name).exists()) {
            File f = new File(configFolder + '/' + name);
            f.mkdir();
        }

    }

    public static File createFile(String name) throws IOException {
        File f = new File(configFolder + "/" + name);
        if (!f.exists()) {
            f.createNewFile();
        }
        return f;
    }

    public static File getFile(String name) {
        return new File(configFolder + "/" + name);
    }

    public static String getContent(String name) throws IOException {
        File f = getFile(name);
        if (!f.exists()) return "";
        return Files.readString(f.toPath(), StandardCharsets.UTF_8);
    }

    public static void appendToFile(String name, String content) throws IOException {
        Files.writeString(getFile(name).toPath(), content + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    public static void writeToFile(String name, String content) throws IOException {
        clearFile(name);
        Files.writeString(getFile(name).toPath(), content + "\n", StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    public static void clearFile(String name) throws IOException {
        Files.writeString(getFile(name).toPath(), "", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

}
