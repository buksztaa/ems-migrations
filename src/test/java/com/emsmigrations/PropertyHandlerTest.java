package com.emsmigrations;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by adambuksztaler on 19/01/16.
 */
public class PropertyHandlerTest extends TestCase {

    File migrationsDir = new File("build/migrations");

    @Override
    protected void setUp() throws Exception {
        migrationsDir.renameTo(new File("build/migrations_" + System.currentTimeMillis()));
        if (migrationsDir.exists()) throw new Exception("Could not delete " + migrationsDir.getAbsolutePath() + ". Consider cleaning the project first.");
        migrationsDir.mkdir();

        Properties properties = new Properties();
        properties.setProperty("key1", "value1");
        properties.setProperty("key2", "value2");
        properties.setProperty("key3", "value3");

        properties.store(new FileWriter(new File(migrationsDir, PropertyHandler.PROPERTY_FILE)), null);
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @Test
    public void testPropertiesFileOnly() {
        PropertyHandler handler = PropertyHandler.create(migrationsDir.getPath());

        assertEquals("value1", handler.getProperty("key1"));
        assertEquals("value2", handler.getProperty("key2"));
        assertEquals("value3", handler.getProperty("key3"));
        assertNull(handler.getProperty("keyNull"));
    }

    @Test
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

    @Test
    public void testNonExistingPropertyFile() {
        PropertyHandler handler = PropertyHandler.create("abrakadabra");

        assertNull(handler.getProperty("anyKey"));
        assertFalse(handler.propertyFileExists);
    }

    @Test
    public void testPrppertiesFileAndgPropertiesMap() {
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
