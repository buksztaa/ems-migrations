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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File-based implementation of <code>MigrationManager</code> interface.
 */
public class FileMigrationManager extends AbstractMigrationManager implements Utils{

    public final String migrationDir;

    private final FileHandler fileHandler;

    private FileMigrationManager(String migrationsDir, EmsConnection connection) throws MigrationException{
        super(connection);
        this.migrationDir = migrationsDir;
        this.fileHandler = FileHandler.create(migrationsDir);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static FileMigrationManager create(String migrationsDir, EmsConnection connection) throws MigrationException{
        return new FileMigrationManager(migrationsDir, connection);
    }

    @Override
    public void createMigration(String description) throws MigrationException{
        fileHandler.createMigration(null, null, description);
    }

    @Override
    public Map<String, Boolean> migrate() throws MigrationException{
        int version = fileHandler.getLatestMigrationNumber();
        return migrate(version);
    }

    @Override
    public Map<String, Boolean> migrate(int version) throws MigrationException{
        Map<String, Boolean> result = new HashMap();
        int remoteVersion = jmsHandler.getVersion();

        if (remoteVersion >= version) {
            throw new MigrationException("Server is already the same or higher version (" + remoteVersion + ").");
        }

        List<String> migrations = fileHandler.getMigrationsUp(remoteVersion + 1, version);


        for (String m : migrations) {
            result.put(new File(m).getName(), emsAdminHandler.execute(m, null));
        }

        jmsHandler.setVersion(version);

        return result;
    }

    @Override
    public Map<String, Boolean> rollback(int version) throws MigrationException{
        Map<String, Boolean> result = new HashMap();
        int remoteVersion = jmsHandler.getVersion();

        if (remoteVersion <= version) {
            throw new MigrationException(("Server is already the same or lower version" + remoteVersion + ")."));
        }

        List<String> migrations = fileHandler.getMigrationsDown(version + 1, remoteVersion);

        for (String m : migrations) {
            result.put(new File(m).getPath(), emsAdminHandler.execute(m, null));
        }

        jmsHandler.setVersion(version);

        return result;
    }

    @Override
    public int checkVersion() throws MigrationException{
        return jmsHandler.getVersion();
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

}
