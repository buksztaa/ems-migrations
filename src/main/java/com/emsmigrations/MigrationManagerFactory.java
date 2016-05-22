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
