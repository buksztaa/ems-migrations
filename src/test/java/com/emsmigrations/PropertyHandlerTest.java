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

import junit.framework.TestCase;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class PropertyHandlerTest extends TestCase {

    File migrationsDir = new File("build/migrations");

    @Override
    protected void setUp() throws Exception {
        migrationsDir.renameTo(new File("build/migrations_" + System.currentTimeMillis()));
        if (migrationsDir.exists()) throw new Exception("Could not delete " + migrationsDir.getAbsolutePath() + ". Consider cleaning the project first.");
        migrationsDir.mkdir();

        java.util.Properties properties = new java.util.Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        properties.setProperty("key3", "value3");

        properties.store(new FileWriter(new File(migrationsDir, Properties.CONF.defaultValue +  PropertyHandler.PROPERTY_EXT)), null);
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public void testPropertiesFileOnly() {
        PropertyHandler handler = PropertyHandler.create(migrationsDir.getPath());

        assertEquals("value1", handler.getProperty("key1"));
        assertEquals("value2", handler.getProperty("key2"));
        assertEquals("value3", handler.getProperty("key3"));
        assertNull(handler.getProperty("keyNull"));
    }

    public void testPropertiesMapOnly() {
        Map<String, String> properties = new HashMap();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        properties.put("key3", "value3");


        PropertyHandler handler = PropertyHandler.create(properties);

        assertEquals("value1", handler.getProperty("key1"));
        assertEquals("value2", handler.getProperty("key2"));
        assertEquals("value3", handler.getProperty("key3"));
        assertNull(handler.getProperty("keyNull"));
    }

    public void testNonExistingPropertyFile() {
        PropertyHandler handler = PropertyHandler.create("abrakadabra");

        assertNull(handler.getProperty("anyKey"));
        assertFalse(handler.propertyFileExists);
    }

    public void testPropertiesFileAndPropertiesMap() {
        Map<String, String> properties = new HashMap();
        properties.put("key1", "value1");
        properties.put("key2", "thisIsOverriden");
        properties.put("key3", "value3");
        properties.put("key4", "value4");

        PropertyHandler handler = PropertyHandler.create(migrationsDir.getPath(), properties);

        assertEquals("value1", handler.getProperty("key1"));
        assertEquals("thisIsOverriden", handler.getProperty("key2"));
        assertEquals("value3", handler.getProperty("key3"));
        assertEquals("value4", handler.getProperty("key4"));
        assertNull(handler.getProperty("keyNull"));
    }

}
