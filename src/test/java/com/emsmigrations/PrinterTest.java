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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adambuksztaler on 08/06/16.
 */
public class PrinterTest extends ExtendedTestCase {

    PrintStream originalOut = System.out;
    TestPrintStream testOut;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testOut = new TestPrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                //NOP
            }
        });

        System.setOut(testOut);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.setOut(originalOut);
    }

    public void testPrintHeader() {
        Printer printer = Printer.get();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", "Mickey");

        assertNoException(() -> {
            printer.printCommand(Commands.HELP, parameters);
        });

        assertEquals("Hello Mickey!", testOut.lines.get(0));
        assertEquals("Where is Pluto?", testOut.lines.get(1));
    }
}
