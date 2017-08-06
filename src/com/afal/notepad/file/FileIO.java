package com.afal.notepad.file;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIO {
    public static void writeTo(File file, String text) throws IllegalArgumentException {
        try (FileWriter out = new FileWriter(file)) {
            out.write(text);
            out.flush();
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't write to specified file.", e);
        }
    }

    public static String readFrom(File file) throws IOException {
        Stream<String> lines = Files.lines(file.toPath());
        return lines.collect(Collectors.joining("\n"));
    }
}
