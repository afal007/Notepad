package com.afal.notepad.file;

import java.io.*;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileIO {
    public static void writeTo(File file, String text) throws IllegalArgumentException {
        try (BufferedReader reader = new BufferedReader(new StringReader(text));
             PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            /*
            * If we use simple FileWriter line breaks are not working for Windows, because from text area we always get
            * \n character for line break. But in Windows \r\n sequence is used for line break. So we have to reread
            * the text and using println insert platform-specific break line characters.
            * */
            reader.lines().forEach(writer::println);
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't write to specified file.", e);
        }
    }

    public static String readFrom(File file) throws IOException {
        Stream<String> lines = Files.lines(file.toPath());
        return lines.collect(Collectors.joining("\n"));
    }
}
