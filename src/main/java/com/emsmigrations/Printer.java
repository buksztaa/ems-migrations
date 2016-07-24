/*
    The MIT License (MIT)

    Copyright (c) 2016

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.
 */
package com.emsmigrations;

import java.io.*;
import java.util.*;

/**
 * This utility class assembles the formatted content to be printed for each command and property
 */
public class Printer {

    private enum Filenames {
        HEADER("header."),
        COMMAND("command."),
        PROPERTY("property.");

        private final String prefix;
        private final String suffix = ".txt";

        Filenames(String prefix) {
            this.prefix = prefix;
        }

        public String getFilename(String object) {
            return "/printer/" + prefix + object + suffix;
        }
    }

    private final LinkedList<String> lines = new LinkedList<>();

    private static Printer instance;

    private Printer() {
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */
    public static Printer get() {
        if (instance == null) {
            instance = new Printer();
        }

        return instance;
    }

    public void printHeader(String header, Map<String, String> parameters) throws MigrationException{
        String filename = Filenames.HEADER.getFilename(header);
        printFile(filename, parameters);
    }

    public void printCommand(Commands command, Map<String, String> parameters) throws MigrationException{
        String filename = Filenames.COMMAND.getFilename(command.commandName);
        printFile(filename, parameters);
    }

    public void printProperty(Properties property, Map<String, String> parameters) throws MigrationException{
        String filename = Filenames.PROPERTY.getFilename(property.propertyName);
        printFile(filename, parameters);
    }

    public void addLine(String line) {
        if (line != null) {
            lines.add(line);
        }
    }


    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private void printFile(String filename, Map<String, String> parameters) throws MigrationException {

        InputStream stream = getClass().getResourceAsStream(filename);

        if (stream == null) {
            throw new MigrationException("Cannot find a resource file: " + filename);
        }

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (lineContainsToken(line, "lines")) {
                    while (!lines.isEmpty()) {
                        printLine(lines.removeFirst());
                    }
                } else {
                    String processedLine = replaceTokensInLine(line, parameters);
                    printLine(processedLine);
                }
            }

        } catch (Exception e) {
            throw new MigrationException("Cannot read a resource file: " + filename, e);
        }
    }

    private void printLine(String line) {
        System.out.println(line);
    }

    private String replaceTokensInLine(String line, Map<String, String> parameters) {
        return parameters.keySet().stream()
                .filter(key -> lineContainsToken(line, key))
                .reduce(line, (newLine, key) -> replaceTokenInLine(line, key, parameters));

    }

    private boolean lineContainsToken(String line, String token) {
        return line.contains("${" + token + "}");
    }

    private String replaceTokenInLine(String line, String key, Map<String, String> parameters) {
        return line.replaceAll("\\$\\{" + key + "\\}", parameters.get(key));
    }

}
