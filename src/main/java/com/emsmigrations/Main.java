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

import java.util.HashMap;
import java.util.Map;

/**
 * Application main class.
 */
public class Main {

    private static final String TAB = "\t";
    private static final String DIR = ".";

    private static MigrationManager manager;
    private static PropertyHandler  propertyHandler;


    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static void main(String[] args) {
        Map<String, String> parameters = parseParameters(args);
        Map<String, String> properties = parseProperties(DIR, parameters);
        runCommand(parameters.get(Properties.COMMAND.propertyName), properties);
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
        println("EMS Migrations");
        println("Running command: " + args.get(Properties.COMMAND.propertyName));
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
        migMap.forEach((k, v) -> println(k + TAB + TAB + TAB + ":" + TAB + (v ? "OK" : "ERR")));
    }

    static void runCommand(String command, Map<String, String> options) {

        try {
            printHeader(options);
            manager = createMigrationManager(options);

            if (Commands.CREATE.commandName.equalsIgnoreCase(command)) {
                manager.createMigration(options.get(Properties.DESC.propertyName));
            } else if (Commands.MIGRATE.commandName.equalsIgnoreCase(command)) {
                Map<String, Boolean> migMap = manager.migrate(getIntFromString(options.get(Properties.VER.propertyName)));
                printMigrationSummary(migMap);
            } else if (Commands.ROLLBACK.commandName.equalsIgnoreCase(command)) {
                Map<String, Boolean> migMap = manager.rollback(getIntFromString(options.get(Properties.VER.propertyName)));
                printMigrationSummary(migMap);
            } else if (Commands.CHECK_VERSION.commandName.equalsIgnoreCase(command)) {
                int version = manager.checkVersion();
                printVersion(version);
            } else if (Commands.HELP.commandName.equalsIgnoreCase(command)) {
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
        println(TAB + "- " + Commands.CREATE.commandName + " - creates a new migration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + Properties.DESC.propertyName + " - (optional) description of the migration. Cannot contain " +
                "white characters.");
        println();
        println(TAB + "- " + Commands.MIGRATE.commandName + " - migrates to a new version of configuration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + Properties.VER.propertyName + " - (optional) version of the configuration the EMS server " +
                "should be migrated to. If not provided, the servers configuration will be upgraded up to the most " +
                "recent version of the migration script");
        println();
        println(TAB + "- " + Commands.ROLLBACK.commandName + " - rolls back to a previous version of configuration");
        println(TAB + TAB + "Available options:");
        println(TAB + TAB + TAB + "- " + Properties.VER.propertyName + " - (required) version of the configuration the EMS server " +
                "should be downgraded to.");
        println();
        println(TAB + "- " + Commands.CHECK_VERSION.commandName + " - checks migration version of connected EMS server");
        println(TAB + "- " + Commands.HELP.commandName + " - prints this message");
        println();
        println("Available global options:");
        println(TAB + "- " + Properties.URL.propertyName + " - (optional) EMS server connection URL");
        println(TAB + "- " + Properties.USER.propertyName + " - (optional) EMS server connection user");
        println(TAB + "- " + Properties.PW.propertyName + " - (optional) EMS server connection password");
    }


    static MigrationManager createMigrationManager(Map<String, String> options) throws MigrationException {
        String type = options.get(Properties.TYPE.propertyName);
        String paramType = (type == null)? MigrationManagerFactory.DEFAULT : type;
        MigrationManager manager = MigrationManagerFactory.createMigrationManager(paramType, options);

        return manager;
    }

    static Map<String, String> parseParameters(String[] parameters) {
        Map<String, String> result = new HashMap();

        if (parameters == null || parameters.length < 1) throw new RuntimeException("Wrong number of parameters");

        String command = parameters[0];
        if (command == null || !Commands.contains(command)) throw new RuntimeException("Wrong command: " + command);
        result.put(Properties.COMMAND.propertyName, command);

        if (parameters.length > 1) {
            if (parameters[1] == null || parameters[1].length() < 2 || !parameters[1].startsWith("-")) throw new RuntimeException("Wrong format for option: " + parameters[1]);
            for (int i = 1; i < parameters.length; i++) {
                if (parameters[i].startsWith("-")) {
                    String option = parameters[i].substring(1);
                    if (!Properties.contains(option)) throw new RuntimeException("Wrong option: " + option);
                    boolean hasValue = parameters.length >= i + 2 && parameters[i + 1] != null && !"".equals(parameters[i + 1].trim()) && !parameters[i + 1].startsWith("-");
                    result.put(option, hasValue? parameters[i + 1] : null);
                }
            }
        }

        return result;
    }

    static void createPropertyHandler(String dir, Map<String, String> properties) {
        if (propertyHandler == null) {
            propertyHandler = PropertyHandler.create(dir, properties);
        }
    }

    static Map<String, String> parseProperties(String dir, Map<String, String> properties) {
        createPropertyHandler(dir, properties);
        return propertyHandler.getProperties();
    }

}
