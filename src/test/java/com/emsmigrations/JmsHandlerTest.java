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
import java.util.Properties;
import java.util.Random;

public class JmsHandlerTest extends ExtendedTestCase {

    private JmsHandler handler;

    @Override
    protected void setUp() throws Exception {
        Properties props = new Properties();
        props.load(new FileReader(new File("build/resources/test/test.properties")));

        EmsConnection connection = EmsConnection.create(
                props.getProperty("ems.connection.url"),
                props.getProperty("ems.connection.username"),
                props.getProperty("ems.connection.password"),
                props.getProperty("ems.installation.location")
        );
        handler = JmsHandler.create(connection);
    }

    public void testSetAndGetVersion() {
        Random r = new Random();
        int v = r.nextInt(1000);
        final int[] version = new int[1];
        assertNoException(() -> {
            handler.openConnection();
            handler.setVersion(v);
            handler.closeConnection();

            handler.openConnection();
            version[0] = handler.getVersion();
            handler.closeConnection();
        });

        assertEquals(v, version[0]);
    }

    public void testSetAndGetInitialVersion() {
        final int[] version = new int[2];
        assertNoException(() -> {
            handler.openConnection();
            handler.setVersion(0);
            handler.closeConnection();

            handler.openConnection();
            version[0] = handler.getVersion();
            handler.closeConnection();

            handler.openConnection();
            handler.setVersion(1);
            handler.closeConnection();

            handler.openConnection();
            version[1] = handler.getVersion();
            handler.closeConnection();
        });

        assertEquals(0, version[0]);
        assertEquals(1, version[1]);
    }
}
