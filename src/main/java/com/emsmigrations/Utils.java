/*
    The MIT License (MIT)

    Copyright (c) 2016 Adam Buksztaler

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Various utility methods used across the application.
 */
public interface Utils {

    default void println(String message) {
        System.out.println(message);
    }

    default <T> T nvl(T value1, T value2) {
        return (value1 == null || value1.toString().isEmpty()) ? value2 : value1;
    }

    default int extractMigrationNumber(String migrationFilePath) {
        int result = -1;
        File file = new File(migrationFilePath);
        String migrationFileName = file.getName();
        if (migrationFileName != null && migrationFileName.matches("^[0-9]{5}_.+\\..+")) {
            result = Integer.parseInt(migrationFileName.substring(0, 5));
        }

        return result;
    }

    default int extractLargestSuccessfulMigrationNumber(Map<String, Boolean> migrationResults, int initialValue) {
        final int[] result = {initialValue};
        if (migrationResults != null) {
            migrationResults.entrySet().forEach(entry -> {
                if (entry.getValue()) {
                    result[0] = Integer.max(result[0], extractMigrationNumber(entry.getKey()));
                }
            });
        }

        return result[0];
    }

    default int extractLowestSuccessfulMigrationNumber(Map<String, Boolean> migrationResults, int initialValue) {
        final int[] result = {initialValue};
        if (migrationResults != null) {
            migrationResults.entrySet().forEach(entry -> {
                if (entry.getValue() && extractMigrationNumber(entry.getKey()) > -1) {
                    result[0] = Integer.min(result[0], extractMigrationNumber(entry.getKey()));
                }
            });
        }

        return result[0];
    }

    default <T> List<T> sublistAfter(List<T> list, T element) {
        List<T> sublist = new ArrayList<>();

        if (list != null && element != null && list.contains(element)) {
            sublist = list.subList(list.indexOf(element), list.size());
        }

        return sublist;
    }

}
