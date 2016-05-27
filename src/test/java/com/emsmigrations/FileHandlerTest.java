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

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileHandlerTest extends ExtendedTestCase {

    File migrationsDir = new File("build/migrations");
    File upDir = FileHandler.Direction.UP.getDir(migrationsDir);
    File downDir = FileHandler.Direction.DOWN.getDir(migrationsDir);
    FileHandler fileHandler;

    @Override
    protected void setUp() throws Exception {
        migrationsDir.renameTo(new File("build/migrations_" + System.currentTimeMillis()));
        if (migrationsDir.exists()) throw new Exception("Could not delete " + migrationsDir.getAbsolutePath() + ". Consider cleaning the project first.");
        migrationsDir.mkdir();
        fileHandler = FileHandler.create(migrationsDir.getPath());
    }

    protected void setUpMigrations() throws Exception {
        Arrays.asList(upDir.listFiles()).forEach(f -> f.delete());
        Arrays.asList(downDir.listFiles()).forEach(f -> f.delete());

        fileHandler.createMigration(null, null, "Migration1");
        fileHandler.createMigration(null, null, "Migration2");
        fileHandler.createMigration(null, null, "Migration3");
        fileHandler.createMigration(null, null, "Migration4");
        fileHandler.createMigration(null, null, "Migration5");
        fileHandler.createMigration(null, null, "Migration6");
        fileHandler.createMigration(null, null, "Migration7");
        fileHandler.createMigration(null, null, "Migration8");

        new File(upDir, "nonimportantfile1.txt").createNewFile();
        new File(downDir, "nonimportantfile2.txt").createNewFile();
    }

    @Override
    protected void tearDown() throws Exception {
        migrationsDir.delete();
    }

    public void testCreateFoldersAfterInit() {
        assertTrue(upDir.exists() && upDir.isDirectory());
        assertTrue(downDir.exists() && downDir.isDirectory());
    }

    public void testCreateMigrationWithNoContentNoDescription() {
        try {
            fileHandler.createMigration(null, null, null);
        } catch (MigrationException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(1, upDir.listFiles().length);
        assertEquals(1, downDir.listFiles().length);
        assertTrue(upDir.listFiles()[0].getName().matches("00001_[0-9]{12}\\.up"));
        assertTrue(downDir.listFiles()[0].getName().matches("00001_[0-9]{12}\\.down"));
    }

    public void testCreateMigrationWithContentAndDescription() {
        try {
            fileHandler.createMigration("UpContent", "DownContent", "TestMigration");
        } catch (MigrationException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(1, upDir.listFiles().length);
        assertEquals(1, downDir.listFiles().length);
        assertTrue(upDir.listFiles()[0].getName().equals("00001_TestMigration.up"));
        assertTrue(downDir.listFiles()[0].getName().equals("00001_TestMigration.down"));
        assertEquals(9, upDir.listFiles()[0].length());
        assertEquals(11, downDir.listFiles()[0].length());
    }

    public void testCreateMultipleMigrationsWithoutContentWithTitle() {
        try {
            fileHandler.createMigration(null, null, "Migration1");
            fileHandler.createMigration(null, null, "Migration2");
            fileHandler.createMigration(null, null, "Migration3");
        } catch (MigrationException e) {
            e.printStackTrace();
            fail();
        }

        assertEquals(3, upDir.listFiles().length);
        assertEquals(3, downDir.listFiles().length);
        assertEquals("00001_Migration1.up", upDir.listFiles()[0].getName());
        assertEquals("00002_Migration2.up", upDir.listFiles()[1].getName());
        assertEquals("00003_Migration3.up", upDir.listFiles()[2].getName());
        assertEquals("00001_Migration1.down", downDir.listFiles()[0].getName());
        assertEquals("00002_Migration2.down", downDir.listFiles()[1].getName());
        assertEquals("00003_Migration3.down", downDir.listFiles()[2].getName());
    }

    public void testGetMigrationsUpSuccess() {
        try {
            setUpMigrations();

            List<String> up4to8 = fileHandler.getMigrationsUp(4, 8);

            assertEquals(5, up4to8.size());
            assertContains(up4to8, "Migration4");
            assertContains(up4to8, "Migration5");
            assertContains(up4to8, "Migration6");
            assertContains(up4to8, "Migration7");
            assertContains(up4to8, "Migration8");

            List<String> up4to4 = fileHandler.getMigrationsUp(4, 4);

            assertEquals(1, up4to4.size());
            assertContains(up4to4, "Migration4");

            List<String> up0to2 = fileHandler.getMigrationsUp(0, 2);

            assertEquals(2, up0to2.size());
            assertContains(up0to2, "Migration1");
            assertContains(up0to2, "Migration2");

            List<String> up8to10 = fileHandler.getMigrationsUp(8, 10);

            assertEquals(1, up8to10.size());

            List<String> up0to0 = fileHandler.getMigrationsUp(0, 0);
            assertEquals(0, up0to0.size());

        } catch(Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testGetMigrationsDownSuccess() {
        try {
            setUpMigrations();

            List<String> down8to4 = fileHandler.getMigrationsDown(4, 8);

            assertEquals(5, down8to4.size());
            assertContains(down8to4, "Migration4");
            assertContains(down8to4, "Migration5");
            assertContains(down8to4, "Migration6");
            assertContains(down8to4, "Migration7");
            assertContains(down8to4, "Migration8");

            List<String> down4to4 = fileHandler.getMigrationsDown(4, 4);

            assertEquals(1, down4to4.size());
            assertContains(down4to4, "Migration4");

            List<String> down2to0 = fileHandler.getMigrationsDown(2, 0);

            assertEquals(2, down2to0.size());
            assertContains(down2to0, "Migration1");
            assertContains(down2to0, "Migration2");

            List<String> down10to8 = fileHandler.getMigrationsDown(8, 10);

            assertEquals(1, down10to8.size());

            List<String> down0to0 = fileHandler.getMigrationsDown(0, 0);
            assertEquals(0, down0to0.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
