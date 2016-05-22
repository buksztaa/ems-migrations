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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adambuksztaler on 12/01/16.
 */
public abstract class AbstractMigrationManager implements MigrationManager {

    public final List<EmsConnection> connections = new ArrayList();

    protected String activeConnection;
    protected final EmsAdminHandler emsAdminHandler;
    protected final JmsHandler jmsHandler;


    protected AbstractMigrationManager(EmsConnection connection) throws MigrationException{
        addConnection(connection);
        setActiveConnection(connection.name);
        this.emsAdminHandler = EmsAdminHandler.create(connection);
        this.jmsHandler = JmsHandler.create(connection);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    @Override
    public void addConnection(EmsConnection connection) throws MigrationException{
        this.connections.add(connection);
    }

    @Override
    public void addConnection(EmsConnection connection, boolean setActive) throws MigrationException{
        this.connections.add(connection);
        setActiveConnection(connection.name);
    }

    @Override
    public void setActiveConnection(String name) throws MigrationException {
        if (connectionExists(name)) {
            activeConnection = name;
            EmsConnection connection = getConnectionByName(name);
            emsAdminHandler.setConnection(connection);
            jmsHandler.setConnection(connection);
        } else {
            throw new MigrationException("Error while setting an active connection. Connection does not exist: " + name);
        }
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private boolean connectionExists(String name) {
        final boolean[] result = {false};
        connections.forEach(c -> {
            if (name != null && name.equals(c.name)) {
                result[0] = true;
            }
        });
        return result[0];
    }

    private EmsConnection getConnectionByName(String name) {
        final EmsConnection[] result = {null};
        connections.forEach(c -> {
                if (name != null && name.equals(c.name)) {
                    result[0] = c;
                }
        });

        return result[0];
    }

}
