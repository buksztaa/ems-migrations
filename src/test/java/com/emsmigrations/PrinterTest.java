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
