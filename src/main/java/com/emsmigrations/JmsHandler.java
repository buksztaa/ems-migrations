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

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles JMS connection.
 */
public class JmsHandler implements Utils{

    private EmsConnection connection;
    private Connection conn;

    public static final String DEFAULT_MESSAGE_VERSION_PROPERTY = "ems-m.version";

    private JmsHandler(EmsConnection connection) {
        this.connection = connection;
    }
    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */
    public static JmsHandler create(EmsConnection connection) {
        return new JmsHandler(connection);
    }


    public int getVersion() throws MigrationException{
        return executeEmsOperation((session, consumer, producer, destination, result) -> {
            Message message;
            Message prevMessage = null;
            while((message = consumer.receive(2000)) != null) {
                if (message.propertyExists(DEFAULT_MESSAGE_VERSION_PROPERTY)) {
                    result.setVersion(message.getIntProperty(DEFAULT_MESSAGE_VERSION_PROPERTY));
                }

                if (prevMessage != null) {
                    prevMessage.acknowledge();
                }
                prevMessage = message;
            }
        });
    }

    public void setVersion(int version) throws MigrationException{
        executeEmsOperation((session, consumer, producer, destination, result) -> {
            List<Message> messages = new ArrayList<>();
            Message message;
            while((message = consumer.receive(2000)) != null) {
                messages.add(message);
            }

            TextMessage newMessage = session.createTextMessage();
            newMessage.setIntProperty(DEFAULT_MESSAGE_VERSION_PROPERTY, version);
            newMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(destination, newMessage);

            for (Message m : messages) {
                m.acknowledge();
            }

            result.setVersion(version);
        });
    }

    public void openConnection() throws MigrationException{
        if (conn == null) {
            try {
                ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(connection.url);
                conn = factory.createConnection(connection.user, connection.password);
            } catch (JMSException e) {
                throw new MigrationException("could not create Connection", e);
            }
        }
    }

    public void closeConnection() throws MigrationException {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (JMSException e) {
            throw new MigrationException("Could not close connection", e);
        }
    }

    public void setConnection(EmsConnection connection) {
        this.connection = connection;
    }


    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private int executeEmsOperation(EmsOperation operation) throws MigrationException {

        if (conn == null) {
            throw new MigrationException("Connection not initialized");
        }

        EmsOperationResult result = new EmsOperationResult();

        Session session = null;

        try {
            session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(connection.queueName);
            MessageConsumer consumer = session.createConsumer(destination);
            MessageProducer producer = session.createProducer(null);
            conn.start();
            operation.execute(session, consumer, producer, destination, result);
        } catch (Exception e) {
            throw new MigrationException("Could not establish ems connection", e);
        } finally {
        }

        return result.getVersion();
    }

    private interface EmsOperation {
        void execute(Session session, MessageConsumer consumer, MessageProducer producer, Destination destination, EmsOperationResult result) throws Exception;
    }

    private class EmsOperationResult {

        private int version;

        void setVersion(int version) {
            this.version = version;
        }

        int getVersion() {
            return version;
        }
    }

}
