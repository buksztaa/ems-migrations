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

import java.util.Map;

/**
 * Created by adambuksztaler on 10/01/16.
 */
public interface MigrationManager {

    void createMigration(String description) throws MigrationException;

    Map<String, Boolean> migrate() throws MigrationException;

    Map<String, Boolean> migrate(int version) throws MigrationException;

    Map<String, Boolean> rollback(int version) throws MigrationException;

    int checkVersion() throws MigrationException;

    void addConnection(EmsConnection connection) throws MigrationException;

    void addConnection(EmsConnection connection, boolean setActive) throws MigrationException;

    void setActiveConnection(String name) throws MigrationException;
}
