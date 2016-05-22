/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.emsmigrations;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adambuksztaler on 10/01/16.
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
