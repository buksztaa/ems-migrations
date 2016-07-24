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

import java.util.Map;

/**
 * Creates <code>MigrationManager</code> based on type provided (file migrations are the default).
 */
public class MigrationManagerFactory {

    public static final String DEFAULT = Properties.TYPE.defaultValue;

    private MigrationManagerFactory() {

    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static MigrationManager createMigrationManager(String type, Map<String, String> parameters) throws MigrationException{
        MigrationManager result;
        String migrationsDir    = parameters.get(Properties.DIR.propertyName);
        String connectionUrl    = parameters.get(Properties.URL.propertyName);
        String connectionUser   = parameters.get(Properties.USER.propertyName);
        String connectionPw     = parameters.get(Properties.PW.propertyName);
        String emsHome          = parameters.get(Properties.EMSHOME.propertyName);

        EmsConnection connection = EmsConnection.create(connectionUrl, connectionUser, connectionPw, emsHome);

        if (DEFAULT.equals(type)) {
            if (migrationsDir == null)  {
                throw new MigrationException("Cannot create MigrationManager. Param " + Properties.DIR.propertyName + " not provided.");
            }

            result =  FileMigrationManager.create(migrationsDir, connection);
        } else {
            throw new MigrationException("Unknown MigrationManager type: " + type);
        }

        return result;
    }

}
