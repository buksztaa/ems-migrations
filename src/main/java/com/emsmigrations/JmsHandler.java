/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.emsmigrations;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by adambuksztaler on 22/02/16.
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
        try {
            ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(connection.url);
            conn = factory.createConnection(connection.user, connection.password);
        } catch (JMSException e) {
            throw new MigrationException("could not create Connection", e);
        }
    }

    public void closeConnection() throws MigrationException {
        try {
            conn.close();
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

        if (connection == null) {
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
