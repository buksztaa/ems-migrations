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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles EMS Admin invocation.
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
        if (password != null && password.trim().length() > 0) {
            sb.append(commandOption(OPT_PASSWORD, password));
        }
        if (options != null) {
            options.forEach((k, v) -> sb.append(commandOption(k, v)));
        }

        return sb.toString();
    }

    private String commandOption(String option, String value) {
        return (value == null) ? "" : "-" + option + " " + value + " ";
    }

}
