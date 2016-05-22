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

/**
 * Created by adambuksztaler on 10/01/16.
 */
public class EmsConnection implements Utils{

    public static final String DEFAULT_NAME = "default";
    public final static String DEFAULT_INITIAL_CONTEXT_FACTORY = "com.tibco.tibjms.naming.TibjmsInitialContextFactory";
    public final static String DEFAULT_CONNECTION_FACTORY = "QueueConnectionFactory";
    public final static String DEFAULT_QUEUE_NAME = "q.ems-migrations.server.version";

    public final String name;
    public final String url;
    public final String user;
    public final String password;
    public final String emsHome;
    public final String initialContextFactory;
    public final String connectionFactory;
    public final String queueName;

    private EmsConnection(String name,
                          String url,
                          String user,
                          String password,
                          String emsHome,
                          String initialContextFactory,
                          String connectionFactory,
                          String queueName
    ) {
        this.name                   = name;
        this.url                    = url;
        this.user                   = user;
        this.password               = password;
        this.emsHome                = emsHome;
        this.initialContextFactory  = nvl(initialContextFactory, DEFAULT_INITIAL_CONTEXT_FACTORY);
        this.connectionFactory      = nvl(connectionFactory, DEFAULT_CONNECTION_FACTORY);
        this.queueName              = nvl(queueName, DEFAULT_QUEUE_NAME);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static EmsConnection create( String name,
                                        String url,
                                        String user,
                                        String password,
                                        String emsHome,
                                        String initialContextFactory,
                                        String connectionFactory,
                                        String queueName) {
        return new EmsConnection(name, url, user, password, emsHome, initialContextFactory, connectionFactory, queueName);
    }

    public static EmsConnection create( String name,
                                        String url,
                                        String user,
                                        String password,
                                        String emsHome) {
        return new EmsConnection(name, url, user, password, emsHome, null, null, null);
    }

    public static EmsConnection create( String url,
                                        String user,
                                        String password,
                                        String emsHome) {
        return new EmsConnection(DEFAULT_NAME, url, user, password, emsHome, null, null, null);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

}
