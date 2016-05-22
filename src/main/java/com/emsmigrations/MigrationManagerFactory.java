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

import java.util.Map;

/**
 * Created by adambuksztaler on 13/01/16.
 */
public class MigrationManagerFactory {

    public static final String DEFAULTS_TYPE = "file";

    public static final String TYPE_DEFAULT = DEFAULTS_TYPE;

    public static final String PARAM_MIGRATIONS_DIR = "migrationsdir";
    public static final String PARAM_CONNECTION_URL = "url";
    public static final String PARAM_CONNECTION_USER = "user";
    public static final String PARAM_CONNECTION_PW = "pw";
    public static final String PARAM_EMS_HOME = "emshome";

    private MigrationManagerFactory() {

    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static MigrationManager createMigrationManager(String type, Map<String, String> parameters) throws MigrationException{
        MigrationManager result;
        String migrationsDir = parameters.get(PARAM_MIGRATIONS_DIR);
        String connectionUrl = parameters.get(PARAM_CONNECTION_URL);
        String connectionUser = parameters.get(PARAM_CONNECTION_USER);
        String connectionPw = parameters.get(PARAM_CONNECTION_PW);
        String emsHome = parameters.get(PARAM_EMS_HOME);

        EmsConnection connection = EmsConnection.create(connectionUrl, connectionUser, connectionPw, emsHome);

        if (DEFAULTS_TYPE.equals(type)) {
            if (migrationsDir == null)  {
                throw new MigrationException("Cannot create MigrationManager. Param " + PARAM_MIGRATIONS_DIR + " not provided.");
            }

            result =  FileMigrationManager.create(migrationsDir, connection);
        } else {
            throw new MigrationException("Unknown MigrationManager type: " + type);
        }

        return result;
    }

}
