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

/**
 * Represents EMS connection details.
 */
public class EmsConnection implements Utils{

    public static final String DEFAULT_NAME = "default";

    public final String name;
    public final String url;
    public final String user;
    public final String password;
    public final String emsHome;
    public final String initialContextFactory;
    public final String connectionFactory;
    public final String queueName;

    private EmsConnection(String name,
                          String url,
                          String user,
                          String password,
                          String emsHome,
                          String initialContextFactory,
                          String connectionFactory,
                          String queueName
    ) {
        this.name                   = name;
        this.url                    = url;
        this.user                   = user;
        this.password               = password;
        this.emsHome                = emsHome;
        this.initialContextFactory  = nvl(initialContextFactory, Properties.CTXFACTORY.defaultValue);
        this.connectionFactory      = nvl(connectionFactory, Properties.CONFACTORY.defaultValue);
        this.queueName              = nvl(queueName, Properties.QUEUE.defaultValue);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */

    public static EmsConnection create( String name,
                                        String url,
                                        String user,
                                        String password,
                                        String emsHome,
                                        String initialContextFactory,
                                        String connectionFactory,
                                        String queueName) {
        return new EmsConnection(name, url, user, password, emsHome, initialContextFactory, connectionFactory, queueName);
    }

    public static EmsConnection create( String name,
                                        String url,
                                        String user,
                                        String password,
                                        String emsHome) {
        return new EmsConnection(name, url, user, password, emsHome, null, null, null);
    }

    public static EmsConnection create( String url,
                                        String user,
                                        String password,
                                        String emsHome) {
        return new EmsConnection(DEFAULT_NAME, url, user, password, emsHome, null, null, null);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

}
