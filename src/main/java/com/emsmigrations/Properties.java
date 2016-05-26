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

import java.util.Arrays;

/**
    This enumeration contains all the options available for the application interface to be set. They are used across
    the application.
 */
public enum Properties {

    DIR             ("dir",         null,                                                   "Migrations root directory"),
    EMSHOME         ("emshome",     null,                                                   "EMS local installation home directory"),
    URL             ("url",         null,                                                   "EMS connection url"),
    USER            ("user",        "admin",                                                "EMS connection username"),
    PW              ("pw",          "",                                                     "EMS connection password"),
    VER             ("ver",         null,                                                   "Migration version"),
    DESC            ("desc",        null,                                                   "Migration description"),
    TYPE            ("type",        "file",                                                 "Migrations type"),
    COMMAND         ("command",     null,                                                   "Application command"),
    CONFACTORY      ("confactory",  "QueueConnectionFactory",                               "EMS connection factory object"),
    QUEUE           ("queue",       "q.ems-migrations.server.version",                      "EMS migration queue"),
    CTXFACTORY      ("ctxfactory",  "com.tibco.tibjms.naming.TibjmsInitialContextFactory",  "Initial context factory class");

    public final String propertyName;
    public final String propertyDescription;
    public final String defaultValue;


    Properties(String propertyName, String defaultValue, String propertyDescription) {
        this.propertyName           = propertyName;
        this.defaultValue           = defaultValue;
        this.propertyDescription    = propertyDescription;
    }

    public static boolean contains(String propertyName) {
        return Arrays.asList(Properties.values()).stream()
                .anyMatch(p -> p.propertyName.equals(propertyName));
    }


}
