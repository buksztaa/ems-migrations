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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adambuksztaler on 19/02/16.
 */
public class EmsAdminHandler implements Utils{

    public static final String OPT_SERVER = "server";
    public static final String OPT_USER = "user";
    public static final String OPT_PASSWORD = "password";
    public static final String OPT_PWDFILE = "pwdfile";
    public static final String OPT_SCRIPT = "script";
    public static final String OPT_IGNORE = "ignore";


    private  EmsConnection connection;
    private final String emsBin;

    private EmsAdminHandler(EmsConnection connection) throws MigrationException{
        this.connection = connection;
        this.emsBin = connection.emsHome + File.separator + "bin";

        verifyEmsHome();
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */
    public static EmsAdminHandler create(EmsConnection connection) throws MigrationException{
        return new EmsAdminHandler(connection);
    }

    public boolean execute(String script, Map<String, String> options) throws MigrationException {
        boolean result = true;
        String command = getCommand(script, connection.url, connection.user, connection.password, options);
        BufferedReader read = null;
        try {
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();

            read = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line;

            while ((line = read.readLine()) != null) {
                if (line.startsWith("Error:")) {
                    result = false;
                }

                println(line);
            }
        } catch (Exception e) {
            throw new MigrationException("Could not execute command: " + command, e);
        } finally {
            if (read != null) {
                try {
                    read.close();
                } catch (IOException e) {
                    throw new MigrationException("Could not close the input stream", e);
                }
            }
        }

        return result;
    }

    public boolean execute(String script) throws MigrationException {
        return execute(script, new HashMap<>());
    }

    public void  setConnection(EmsConnection connection) {
        this.connection = connection;
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */
    private void verifyEmsHome() throws MigrationException{
        File binDir = new File(emsBin);

        if (!binDir.exists() || !binDir.isDirectory()) {
            throw new MigrationException("Could not find a directory " + emsBin);
        }

        File[] emsadminExecs = binDir.listFiles((f, n) -> n.startsWith("tibemsadmin"));

        if (emsadminExecs.length == 0) {
            throw new MigrationException("Could not find tibemsadmin executable program within directory " + emsBin);
        }
    }

    private String getExecutableName() throws MigrationException {
        String executableName = "tibemsadmin";
        String osName = System.getProperty("os.name");

        if (osName == null) {
            throw new MigrationException("Operating system type cannot be determined");
        }

        if (osName.toLowerCase().indexOf("win") >= 0) {
            executableName += ".exe";
        }

        return executableName;
    }

    private String getCommand(String script, String serverUrl, String user, String password, Map<String, String> options) throws MigrationException {
        StringBuilder sb = new StringBuilder();
        sb.append(emsBin + File.separator + getExecutableName() + " ");
        sb.append(commandOption(OPT_SCRIPT, script));
        sb.append(commandOption(OPT_SERVER, serverUrl));
        sb.append(commandOption(OPT_USER, user));
        sb.append(commandOption(OPT_PASSWORD, password));
        options.forEach((k, v) -> sb.append(commandOption(k, v)));

        return sb.toString();
    }

    private String commandOption(String option, String value) {
        return (value == null) ? "" : "-" + option + " " + value + " ";
    }

}
