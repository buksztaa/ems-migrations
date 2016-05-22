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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by adambuksztaler on 14/01/16.
 */
public class PropertyHandler {

    public static final String PROPERTY_FILE = "ems-m.properties";

    public final String propertiesDir;
    public final boolean propertyFileExists;

    private final File propertiesFile;
    private final Map<String, String> properties;

    private PropertyHandler(String propertiesDir, Map<String, String> properties) {
        this.propertiesDir = propertiesDir;
        this.propertiesFile = new File(propertiesDir, PROPERTY_FILE);
        this.propertyFileExists = propertiesFile.exists() && propertiesFile.isFile();

        Properties fileProperties = loadProperties(propertiesFile);
        this.properties = mergeProperties(fileProperties, properties);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static PropertyHandler create(String propertiesDir, Map<String, String> properties) {
        return new PropertyHandler(propertiesDir, properties);
    }

    public static PropertyHandler create(String propertiesDir) {
        return new PropertyHandler(propertiesDir, Collections.emptyMap());
    }

    public static PropertyHandler create(Map<String, String> properties) {
        return new PropertyHandler(nonExistingPath(), properties);
    }

    public String getProperty(String value) {
        return properties.get(value);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private Properties loadProperties(File propertiesFile) {
        Properties result = new Properties();
        try {
            result.load(new FileReader(propertiesFile));
        } catch (IOException e) {}

        return result;
    }

    private Map<String, String> mergeProperties(Properties fileProperties, Map<String, String> properties) {
        Map<String, String> result = new HashMap();
        fileProperties.keySet().forEach(k -> result.put("" + k, fileProperties.getProperty((String)k)));
        result.putAll(properties);

        return result;
    }

    private static String nonExistingPath() {
        File result = null;
        while (result == null || result.exists()) {
            result = new File("" + System.currentTimeMillis());
        }

        return result.getPath();
    }

}
