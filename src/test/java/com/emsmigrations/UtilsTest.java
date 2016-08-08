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

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adambuksztaler on 06/08/16.
 */
public class UtilsTest extends ExtendedTestCase implements Utils{

    @Test
    public void testExtractMigrationNumber() {
        assertEquals(3, extractMigrationNumber("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateUsers.down"));
        assertEquals(5, extractMigrationNumber("../migrations/down/00005_CreateUsers.up"));
        assertEquals(-1, extractMigrationNumber("../migrations/down/0005_CreateUsers.up"));
        assertEquals(-1, extractMigrationNumber("../migrations/down/CreateUsers.up"));
        assertEquals(-1, extractMigrationNumber("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down"));
        assertEquals(3, extractMigrationNumber("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down"));
        assertEquals(2, extractMigrationNumber("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down"));
        assertEquals(1, extractMigrationNumber("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down"));
    }

    @Test
    public void testExtractLargestSuccessfulMigrationNumber() {
        Map<String, Boolean> map1 = new LinkedHashMap<>();
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.up", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.up", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.up", false);

        assertEquals(2, extractLargestSuccessfulMigrationNumber(map1, 0));

        Map<String, Boolean> map2 = new LinkedHashMap<>();
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.up", false);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.up", false);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.up", false);

        assertEquals(0, extractLargestSuccessfulMigrationNumber(map2, 0));

        Map<String, Boolean> map3 = new LinkedHashMap<>();
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0001_CreateUsers.up", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0002_CreateQueues.up", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0003_CreateTopics.up", true);

        assertEquals(0, extractLargestSuccessfulMigrationNumber(map3, 0));

        Map<String, Boolean> map4 = new LinkedHashMap<>();
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.up", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.up", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.up", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/post.up", true);

        assertEquals(3, extractLargestSuccessfulMigrationNumber(map4, 0));

        Map<String, Boolean> map5 = null;
        assertEquals(0, extractLargestSuccessfulMigrationNumber(map5, 0));

    }

    @Test
    public void testExtractLowestSuccessfulMigrationNumber() {
        Map<String, Boolean> map1 = new LinkedHashMap<>();
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down", true);
        map1.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down", false);

        assertEquals(2, extractLowestSuccessfulMigrationNumber(map1, 3));

        Map<String, Boolean> map2 = new LinkedHashMap<>();
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down", true);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down", true);
        map2.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down", true);

        assertEquals(1, extractLowestSuccessfulMigrationNumber(map2, 3));

        Map<String, Boolean> map3 = new LinkedHashMap<>();
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0003_CreateTopics.down", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0002_CreateQueues.down", true);
        map3.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/0001_CreateUsers.down", true);

        assertEquals(3, extractLowestSuccessfulMigrationNumber(map3, 3));

        Map<String, Boolean> map4 = new LinkedHashMap<>();
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down", true);
        map4.put("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/post.down", true);

        assertEquals(1, extractLowestSuccessfulMigrationNumber(map4, 3));

        Map<String, Boolean> map5 = null;
        assertEquals(3, extractLowestSuccessfulMigrationNumber(map5, 3));

    }

    @Test
    public void testSublistAfter() {
        List<String> list = new ArrayList<>();
        list.add("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down");
        list.add("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down");
        list.add("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down");
        list.add("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down");
        list.add("/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/post.down");

        assertEquals(0, sublistAfter(list, "nonexisting").size());
        assertEquals(5, sublistAfter(list, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/pre.down").size());
        assertEquals(4, sublistAfter(list, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00003_CreateTopics.down").size());
        assertEquals(3, sublistAfter(list, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00002_CreateQueues.down").size());
        assertEquals(2, sublistAfter(list, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/00001_CreateUsers.down").size());
        assertEquals(1, sublistAfter(list, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/post.down").size());
        assertEquals(0, sublistAfter(list, null).size());
        assertEquals(0, sublistAfter(null, "/Users/me/Sources/ems-migrations/build/distributions/ems-m-0.0.1-SNAPSHOT/bin/../migrations/down/post.down").size());

    }

}
