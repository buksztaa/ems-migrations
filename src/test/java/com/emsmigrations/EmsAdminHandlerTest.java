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

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by adambuksztaler on 20/02/16.
 */
public class EmsAdminHandlerTest extends ExtendedTestCase {

    private File emsBin;

    @Override
    protected void setUp() throws Exception {
        emsBin = new File("bin");
        emsBin.mkdir();
        File tibemsadmin = new File(emsBin, "tibemsadmin");
        tibemsadmin.createNewFile();
    }

    @Override
    protected void tearDown() throws Exception {
        if (emsBin != null) {
            emsBin.delete();
        }
    }

    @Test
    public void testVerifyEmsBinFailure() {
        assertException(() -> EmsAdminHandler.create(EmsConnection.create(null, null, null, "nosuchfolder")));
    }

    @Test
    public void testVerifyEmsBinSuccess() {
        assertNoException(() -> EmsAdminHandler.create(EmsConnection.create(null, null, null, ".")));
    }

    @Test
    public void testExecuteSuccess() throws Exception {

        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("script.scr"))) {
            writer.write("show queues\n");
        }

        EmsAdminHandler handler = EmsAdminHandler.create(EmsConnection.create("local", "localhost:7222", "admin", null, "/Users/adambuksztaler/tibco/ems/8.2"));

        assertTrue(handler.execute("script.scr"));

    }

    @Test
    public void testExecuteFailure() throws Exception {

        try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("script.scr"))) {
            writer.write("show something\n");
        }

        EmsAdminHandler handler = EmsAdminHandler.create(EmsConnection.create("local", "localhost:7222", "admin", null, "/Users/adambuksztaler/tibco/ems/8.2"));

        assertFalse(handler.execute("script.scr"));

    }
}
