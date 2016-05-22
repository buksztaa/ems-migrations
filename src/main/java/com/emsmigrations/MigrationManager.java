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
