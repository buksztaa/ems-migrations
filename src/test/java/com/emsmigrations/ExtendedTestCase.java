package com.emsmigrations;

import junit.framework.TestCase;

import java.util.List;

/**
 * Created by adambuksztaler on 26/01/16.
 */
public abstract class ExtendedTestCase extends TestCase{

    private boolean runWithErrorHandling(Closure closure) {
        boolean ex = false;
        try {
            closure.execute();
        } catch (Exception e) {
            ex = true;
            e.printStackTrace();
        }

        return ex;
    }

    public void assertException(Closure closure) {
        assertTrue(runWithErrorHandling(closure));
    }

    public void assertNoException(Closure closure) {
        assertFalse(runWithErrorHandling(closure));
    }

    public void assertContains(String container, CharSequence element) {
        if (container == null) {
            fail("Container string is null");
        }
        assertTrue(container.contains(element));
    }

    public void assertContains(List<String> container, CharSequence element) {
        if (container == null) {
            fail("Container collection is null");
        }

        final boolean[] contains = {false};

        container.forEach((s -> contains[0] |=  s.contains(element)));

        assertTrue(contains[0]);
    }

    public void assertNotContains(String container, CharSequence element) {
        if (container != null) {
            assertFalse(container.contains(element));
        }
    }

    public void assertNotContains(List<String> container, CharSequence element) {
        if (container != null) {
            final boolean[] contains = {false};

            container.forEach(s -> contains[0] |= s.contains(element));

            assertFalse(container.contains(element));
        }
    }

}
