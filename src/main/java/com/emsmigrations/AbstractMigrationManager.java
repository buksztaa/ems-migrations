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

import java.util.ArrayList;
import java.util.List;

/**
 * MigrationManager implementation abstraction.
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
