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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * File-based implementation of <code>MigrationManager</code> interface.
 */
public class FileMigrationManager extends AbstractMigrationManager implements Utils{

    public final String migrationDir;

    private final FileHandler fileHandler;

    private FileMigrationManager(String migrationsDir, EmsConnection connection, Class<? extends MigrationExecutor> strategy) throws MigrationException{
        super(connection, strategy);
        this.migrationDir = migrationsDir;
        this.fileHandler = FileHandler.create(migrationsDir);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static FileMigrationManager create(String migrationsDir, EmsConnection connection, Class<? extends MigrationExecutor> strategy) throws MigrationException{
        return new FileMigrationManager(migrationsDir, connection, strategy);
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
        Map<String, Boolean> result = new LinkedHashMap<>();

        if (version < 0 ) {
            version = fileHandler.getLatestMigrationNumber();
        }

        try {
            jmsHandler.openConnection();
            int remoteVersion = jmsHandler.getVersion();

            if (remoteVersion > 0 && remoteVersion >= version) {
                throw new MigrationException("Server is already the same or higher version (" + remoteVersion + ").");
            }

            List<String> migrations = fileHandler.getMigrationsUp(remoteVersion + 1, version);
            List<String> rollbacks = fileHandler.getMigrationsDown(remoteVersion + 1, version);


            MigrationExecutor executor = MigrationExecutor.create(MigrationExecutorTerminateStrategy.class, emsAdminHandler);
            result.putAll(executor.execute(migrations, rollbacks, null));

            jmsHandler.setVersion(executor.getLastSuccessfulUpMigrationNumber(remoteVersion));
        } finally {
            jmsHandler.closeConnection();
        }

        return result;
    }

    @Override
    public Map<String, Boolean> rollback(int version) throws MigrationException{
        Map<String, Boolean> result = new LinkedHashMap<>();

        if (version < 0) {
            throw new MigrationException("Version not provided");
        }

        try {
            jmsHandler.openConnection();
            int remoteVersion = jmsHandler.getVersion();

            if (remoteVersion <= version) {
                throw new MigrationException(("Server is already the same or lower version (" + remoteVersion + ")."));
            }

            List<String> migrations = fileHandler.getMigrationsDown(version + 1, remoteVersion);
            List<String> rollbacks = fileHandler.getMigrationsUp(version + 1, remoteVersion);

            MigrationExecutor executor = MigrationExecutor.create(MigrationExecutorTerminateStrategy.class, emsAdminHandler);
            result.putAll(executor.execute(migrations, rollbacks, null));

            jmsHandler.setVersion(executor.getLastSuccessfulDownMigrationNumber(remoteVersion) - 1);
        } finally {
            jmsHandler.closeConnection();
        }
        return result;
    }

    @Override
    public int checkVersion() throws MigrationException{
        int version;
        try {
            jmsHandler.openConnection();
            version = jmsHandler.getVersion();
        } finally {
            jmsHandler.closeConnection();
        }
        return version;
    }

    public String getLastUpFilePath() {
        return fileHandler.getLastUpFilePath();
    }

    public String getLastDownFilePath() {
        return fileHandler.getLastDownFilePath();
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

}
