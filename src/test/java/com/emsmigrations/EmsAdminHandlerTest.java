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
