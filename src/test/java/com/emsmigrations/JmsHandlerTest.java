package com.emsmigrations;

import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.Random;

/**
 * Created by adambuksztaler on 18/04/16.
 */
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


    @Test
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
}
