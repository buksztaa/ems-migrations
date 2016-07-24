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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Application main class.
 */
public class Main {

    private static final String TAB = "  ";
    private static final String PROPS_DIR = "../conf";

    private static FileMigrationManager manager;
    private static PropertyHandler      propertyHandler;
    private static Printer              printer = Printer.get();


    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static void main(String[] args) {
        Map<String, String> parameters = parseParameters(args);
        Map<String, String> properties = parseProperties(PROPS_DIR, parameters);

        Commands command = Commands.findByName(parameters.get(Properties.COMMAND.propertyName));
        runCommand(command, properties);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    static void printHeader(Commands command, Map<String, String> args) throws MigrationException{
        Map<String, String> parameters = new HashMap<>();
        parameters.put("command", command.commandName);
        printer.printHeader("application", parameters);
    }

    static void printError(Exception e) {
        e.printStackTrace();
    }

    static void printHelp() throws MigrationException {
        printer.printCommand(Commands.HELP, Collections.EMPTY_MAP);
    }

    static void printCreate() throws MigrationException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("upFilePath", manager.getLastUpFilePath());
        parameters.put("downFilePath", manager.getLastDownFilePath());
        printer.printCommand(Commands.CREATE, parameters);
    }

    static void printCheckVersion(int version) throws MigrationException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("version", String.valueOf(version));
        printer.printCommand(Commands.CHECK_VERSION, parameters);
    }

    static void printMigrate(Map<String, Boolean> results) throws MigrationException{
        int total = results.size();
        long failed = results.values().stream().filter(v -> v == false).count();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("total", String.valueOf(total));
        parameters.put("failed", String.valueOf(String.valueOf(failed)));
        results.keySet().forEach((k) -> printer.addLine(k + " : " + (results.get(k)? "OK" : "FAILED")));
        printer.printCommand(Commands.MIGRATE, parameters);
    }

    static void runCommand(Commands command, Map<String, String> options) {

        try {
            printHeader(command, options);

            if (command.requiresMigrationManager) {
                manager = createMigrationManager(options);
            }

            if (Commands.CREATE.equals(command)) {
                manager.createMigration(options.get(Properties.DESC.propertyName));
                printCreate();
            } else if (Commands.MIGRATE.equals(command)) {
                Map<String, Boolean> migMap = manager.migrate(getIntFromString(options.get(Properties.VER.propertyName)));
                printMigrate(migMap);
            } else if (Commands.ROLLBACK.equals(command)) {
                Map<String, Boolean> migMap = manager.rollback(getIntFromString(options.get(Properties.VER.propertyName)));
                printMigrate(migMap);
            } else if (Commands.CHECK_VERSION.equals(command)) {
                int version = manager.checkVersion();
                printCheckVersion(version);
            } else if (Commands.HELP.equals(command)) {
                printHelp();
            } else {
                printHelp();
            }
        } catch (Exception e) {
            printError(e);
        }
    }

    static int getIntFromString(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static FileMigrationManager createMigrationManager(Map<String, String> options) throws MigrationException {
        String type = options.get(Properties.TYPE.propertyName);
        String paramType = (type == null)? MigrationManagerFactory.DEFAULT : type;
        FileMigrationManager manager = (FileMigrationManager)MigrationManagerFactory.createMigrationManager(paramType, options);

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
