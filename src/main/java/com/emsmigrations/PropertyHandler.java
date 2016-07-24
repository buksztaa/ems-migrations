/*
    The MIT License (MIT)

    Copyright (c) 2016 Adam Buksztaler

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles retrieval of properties.
 */
public class PropertyHandler {

    public static final String PROPERTY_EXT = ".properties";

    public final String propertiesDir;
    public final boolean propertyFileExists;

    private final File propertiesFile;
    private final Map<String, String> properties;

    private PropertyHandler(String propertiesDir, Map<String, String> properties) {
        String fileBase = Properties.getPropertyFromMap(Properties.CONF, properties);

        this.propertiesDir = propertiesDir;
        this.propertiesFile = new File(propertiesDir, fileBase + PROPERTY_EXT);
        this.propertyFileExists = propertiesFile.exists() && propertiesFile.isFile();

        java.util.Properties fileProperties = loadProperties(propertiesFile);
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

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private java.util.Properties loadProperties(File propertiesFile) {
        java.util.Properties result = new java.util.Properties();
        try {
            result.load(new FileReader(propertiesFile));
        } catch (IOException e) {}

        return result;
    }

    private Map<String, String> mergeProperties(java.util.Properties fileProperties, Map<String, String> properties) {
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
