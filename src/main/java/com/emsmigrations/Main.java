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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adambuksztaler on 10/01/16.
 */
public class Main {

    private static final String VERSION =           "0.1";

    private static final String TAB =               "\t";

    private static final String COM_CREATE =        "create";
    private static final String COM_MIGRATE =       "migrate";
    private static final String COM_ROLLBACK =      "rollback";
    private static final String COM_CHECK_VERSION = "check-version";
    private static final String COM_HELP =          "help";

    private static final String OPT_VERSION =       "ver";
    private static final String OPT_DESCRIPTION =   "desc";
    private static final String OPT_URL =           "url";
    private static final String OPT_USR =           "user";
    private static final String OPT_PASS =          "pw";
    private static final String OPT_TYPE =          "type";
    private static final String OPT_COMMAND =       "command";

    private static final String[] COMMANDS = {COM_CHECK_VERSION, COM_CREATE, COM_HELP, COM_MIGRATE, COM_ROLLBACK};
    private static final String[] OPTIONS = {OPT_DESCRIPTION, OPT_PASS, OPT_TYPE, OPT_URL, OPT_USR, OPT_VERSION};

    private static MigrationManager manager;


    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static void main(String[] args) {
        Map<String, String> options = parseParameters(args);
        runCommand(options.get(OPT_COMMAND), options);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    static void println(String content) {
        System.out.println(content);
    }

    static void println() {
        System.out.println();
    }

    static void printHeader(Map<String, String> args) {
        println("EMS Migrations version " + VERSION);
        println("Running command: " + args.get(OPT_COMMAND));
    }

    static void printVersion(int version) {
        println("Server version: " + version);
    }

    static void printError(String content) {
        println("ERROR: " + content);
    }

    static void printMigrationSummary(Map<String, Boolean> migMap) {
        boolean failed = migMap.values().contains(Boolean.FALSE);
        println("Migrations executed " + ((failed) ? "WITH ERRORS" : "with no errors"));
        println();
        migMap.forEach((k, v) -> println(k + "\t\t\t:\t" + (v ? "OK" : "ERR")));
    }

    static void runCommand(String command, Map<String, String> options) {

        try {
            printHeader(options);
            manager = createMigrationManager(options);

            if (COM_CREATE.equalsIgnoreCase(command)) {
                manager.createMigration(options.get(OPT_DESCRIPTION));
            } else if (COM_MIGRATE.equalsIgnoreCase(command)) {
                Map<String, Boolean> migMap = manager.migrate(getIntFromString(options.get(OPT_VERSION)));
                printMigrationSummary(migMap);
            } else if (COM_ROLLBACK.equalsIgnoreCase(command)) {
                Map<String, Boolean> migMap = manager.rollback(getIntFromString(options.get(OPT_VERSION)));
                printMigrationSummary(migMap);
            } else if (COM_CHECK_VERSION.equalsIgnoreCase(command)) {
                int version = manager.checkVersion();
                printVersion(version);
            } else if (COM_HELP.equalsIgnoreCase(command)) {
                help();
            } else {
                help();
            }
        } catch (Exception e) {
            printError(e.getMessage());
        }
    }

    static int getIntFromString(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static void help() {
        println("Usage: <command> <option=value>...");
        println("Example: create description=new_users");
        println();
        println("Available commands:");
        println();
        println(TAB + "- " + COM_CREATE + " - creates a new migration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + OPT_DESCRIPTION + " - (optional) description of the migration. Cannot contain " +
                "white characters.");
        println();
        println(TAB + "- " + COM_MIGRATE + " - migrates to a new version of configuration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + OPT_VERSION + " - (optional) version of the configuration the EMS server " +
                "should be migrated to. If not provided, the servers configuration will be upgraded up to the most " +
                "recent version of the migration script");
        println();
        println(TAB + "- " + COM_ROLLBACK + " - rolls back to a previous version of configuration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + OPT_VERSION + " - (required) version of the configuration the EMS server " +
                "should be downgraded to.");
        println();
        println(TAB + "- " + COM_CHECK_VERSION + " - checks migration version of connected EMS server");
        println(TAB + "- " + COM_HELP + " - prints this message");
        println();
        println("Available global options:");
        println(TAB + "- " + OPT_URL + " - (optional) EMS server connection URL");
        println(TAB + "- " + OPT_USR + " - (optional) EMS server connection user");
        println(TAB + "- " + OPT_PASS + " - (optional) EMS server connection password");
    }


    static MigrationManager createMigrationManager(Map<String, String> options) throws MigrationException {
        String type = options.get(OPT_TYPE);
        String paramType = (type == null)? MigrationManagerFactory.TYPE_DEFAULT : type;
        MigrationManager manager = MigrationManagerFactory.createMigrationManager(paramType, options);

        return manager;
    }

    static Map<String, String> parseParameters(String[] parameters) {
        Map<String, String> result = new HashMap();

        if (parameters == null || parameters.length < 1) throw new RuntimeException("Wrong number of parameters");

        String command = parameters[0];
        if (command == null || !Arrays.asList(COMMANDS).contains(command)) throw new RuntimeException("Wrong command: " + command);
        result.put(OPT_COMMAND, command);

        if (parameters.length > 1) {
            if (parameters[1] == null || parameters[1].length() < 2 || !parameters[1].startsWith("-")) throw new RuntimeException("Wrong format for option: " + parameters[1]);
            for (int i = 1; i < parameters.length; i++) {
                if (parameters[i].startsWith("-")) {
                    String option = parameters[i].substring(1);
                    if (!Arrays.asList(OPTIONS).contains(option)) throw new RuntimeException("Wrong option: " + option);
                    boolean hasValue = parameters.length >= i + 2 && parameters[i + 1] != null && !"".equals(parameters[i + 1].trim()) && !parameters[i + 1].startsWith("-");
                    result.put(option, hasValue? parameters[i + 1] : null);
                }
            }
        }

        return result;
    }

}
