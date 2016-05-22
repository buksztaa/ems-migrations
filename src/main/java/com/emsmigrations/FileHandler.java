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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by adambuksztaler on 11/01/16.
 */
public class FileHandler {

    public enum Direction {

        UP("up"),
        DOWN("down");

        public final String value;

        Direction(String value) {
            this.value = value;
        }

        public File getDir(File rootDir) {
            return new File(rootDir, value);
        }

        public void mkdir(File rootDir) {
            getDir(rootDir).mkdir();
        }

        public void sort(List<String> migrations) {
            Collections.sort(migrations);

            if (DOWN.equals(this)) {
                Collections.reverse(migrations);
            }
        }


    }

    public final String migrationsDir;

    private final File rootDir;

    private FileHandler(String migrationsDir) {
        this.migrationsDir = migrationsDir;

        this.rootDir = new File(migrationsDir);

        rootDir.mkdir();
        Direction.UP.mkdir(rootDir);
        Direction.DOWN.mkdir(rootDir);
    }


    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static FileHandler create(String migrationsDir) {
        return new FileHandler(migrationsDir);
    }

    public int createMigration(String upContent, String downContent, String description) throws MigrationException {
        try {
            createMigrationFiles(generateFileName(description), upContent, downContent);
        } catch (IOException e) {
            throw new MigrationException("Error while generating migration files", e);
        }

        return getNextMigrationNumber();
    }

    public int getLatestMigrationNumber() {
        return getNextMigrationNumber() -1;
    }

    public List<String> getMigrationsUp(int from, int to) {
        return getMigrations(from, to, Direction.UP);
    }

    public List<String> getMigrationsDown(int from, int to) {
        return getMigrations(from, to, Direction.DOWN);
    }

    public List<String> getMigrationsUp(int from) {
        return getMigrationsUp(from, getLatestMigrationNumber());
    }

    public List<String> getMigrationsDown(int from) {
        return getMigrationsDown(from, getLatestMigrationNumber());
    }


    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private String generateFileName(String description) {
        String newMigrationNumber = String.format("%05d", getNextMigrationNumber());

        Date now = new Date();
        String formattedNow = new SimpleDateFormat("yyMMddHHmmss").format(now);
        String finalDescription = (description != null)? description : formattedNow;

        return newMigrationNumber + "_" + finalDescription;
    }

    private int getNextMigrationNumber() {
        List<File> allFiles = new ArrayList();
        List<File> upFiles = Arrays.asList(Direction.UP.getDir(rootDir).listFiles());
        List<File> downFiles = Arrays.asList(Direction.DOWN.getDir(rootDir).listFiles());
        allFiles.addAll(upFiles);
        allFiles.addAll(downFiles);

        List<Integer> numbers = new ArrayList();
        numbers.add(0);
        allFiles.forEach(f -> {
            String[] tokens = f.getName().split("_");
            if (tokens[0] != null) {
                try {
                    numbers.add(Integer.parseInt(tokens[0]));
                } catch (NumberFormatException e){
                    e.printStackTrace();
                }
            }
        });

        numbers.sort((Integer i1, Integer i2) -> i1 - i2);

        return numbers.get(numbers.size() - 1).intValue() + 1;
    }

    private void createMigrationFiles(String fileName, String upContent, String downContent) throws IOException {
        final File upFile = new File(Direction.UP.getDir(rootDir), fileName + "." + Direction.UP.value);
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(upFile.getPath()))) {
            w.write((upContent != null)? upContent : "");
        } catch (IOException e){
            upFile.delete();
        }

        final File downFile = new File(Direction.DOWN.getDir(rootDir), fileName + "." + Direction.DOWN.value);
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(downFile.getPath()))) {
            w.write((downContent != null) ? downContent : "");
        } catch (IOException e) {
            upFile.delete();
            downFile.delete();
        }
    }

    private List<String> getMigrations(int from, int to, Direction direction) {
        List<String> result = new ArrayList();

        File[] migrationFiles = direction.getDir(rootDir).listFiles((f, n) -> n.matches("^[0-9]{5}_.+\\." + direction.value));
        Arrays.asList(migrationFiles).forEach(mf -> {
            int migrationVersion = Integer.parseInt(mf.getName().substring(0, 5));
            if (migrationVersion >= Integer.min(from, to) && migrationVersion <= Integer.max(from, to)) {
                result.add(mf.getAbsolutePath());
            }
        });

        direction.sort(result);

        return result;
    }


}
