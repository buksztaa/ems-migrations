package com.emsmigrations;

import org.junit.Test;

import java.util.Map;

/**
 * Created by adambuksztaler on 26/01/16.
 */
public class MainTest extends ExtendedTestCase {

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    @Test
    public void testGetIntFromStringSuccess() {
        assertEquals(10, Main.getIntFromString("10"));
        assertEquals(10, Main.getIntFromString("010"));
    }

    @Test
    public void testGetIntFromStringFailure() {
        assertEquals(-1, Main.getIntFromString("thisisnotanumber"));
        assertEquals(-1, Main.getIntFromString("1,4"));
        assertEquals(-1, Main.getIntFromString("12.345623"));
    }

    @Test
    public void testNoParameters() {
        assertException(() -> Main.parseParameters(null));
    }

    @Test
    public void testWrongNumberOfParameters() {
        String[] params = {};
        assertException(() -> Main.parseParameters(params));
    }

    @Test
    public void testWrongCommand() {
        String[] params = {"thisonedoesntexist"};
        assertException(() -> Main.parseParameters(params));
    }

    @Test
    public void testWrongFormatForOption() {
        String[] params = {"create", "wrongoption", "1"};
        assertException(() -> Main.parseParameters(params));
    }

    @Test
    public void testWrongOption() {
        String[] params = {"create", "-nonexisting", "admin"};
        assertException(() -> Main.parseParameters(params));
    }

    @Test
    public void testNoOptions() {
        String[] params = {"create"};
        Map<String, String> result = Main.parseParameters(params);
        assertEquals("create", result.get("command"));
        assertEquals(1, result.keySet().size());
    }

    @Test
    public void testOneOption() {
        String[] params = {"create", "-user", "admin"};
        Map<String, String> result = Main.parseParameters(params);
        assertEquals("admin", result.get("user"));
        assertEquals(2, result.keySet().size());
    }

    @Test
    public void testMultipleOptions() {
        String[] params = {"create", "-user", "admin", "-url", "-desc", "Lorem ipsum"};
        Map<String, String> result = Main.parseParameters(params);
        assertEquals("admin", result.get("user"));
        assertNull(result.get("url"));
        assertTrue(result.containsKey("url"));
        assertEquals("Lorem ipsum", result.get("desc"));
        assertEquals(4, result.keySet().size());
    }

}
