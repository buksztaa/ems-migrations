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

import java.util.Map;

public class MainTest extends ExtendedTestCase {

    @Override
    protected void setUp() throws Exception {

    }

    @Override
    protected void tearDown() throws Exception {

    }

    public void testGetIntFromStringSuccess() {
        assertEquals(10, Main.getIntFromString("10"));
        assertEquals(10, Main.getIntFromString("010"));
    }

    public void testGetIntFromStringFailure() {
        assertEquals(-1, Main.getIntFromString("thisisnotanumber"));
        assertEquals(-1, Main.getIntFromString("1,4"));
        assertEquals(-1, Main.getIntFromString("12.345623"));
    }

    public void testNoParameters() {
        assertException(() -> Main.parseParameters(null));
    }

    public void testWrongNumberOfParameters() {
        String[] params = {};
        assertException(() -> Main.parseParameters(params));
    }

    public void testWrongCommand() {
        String[] params = {"thisonedoesntexist"};
        assertException(() -> Main.parseParameters(params));
    }

    public void testWrongFormatForOption() {
        String[] params = {"create", "wrongoption", "1"};
        assertException(() -> Main.parseParameters(params));
    }

    public void testWrongOption() {
        String[] params = {"create", "-nonexisting", "admin"};
        assertException(() -> Main.parseParameters(params));
    }

    public void testNoOptions() {
        String[] params = {"create"};
        Map<String, String> result = Main.parseParameters(params);
        assertEquals("create", result.get("command"));
        assertEquals(1, result.keySet().size());
    }

    public void testOneOption() {
        String[] params = {"create", "-user", "admin"};
        Map<String, String> result = Main.parseParameters(params);
        assertEquals("admin", result.get("user"));
        assertEquals(2, result.keySet().size());
    }

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
