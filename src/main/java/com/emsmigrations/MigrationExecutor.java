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

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Migration executor with three strategy implementations.
 */
public abstract class MigrationExecutor implements Utils{

    protected final EmsAdminHandler emsHandler;
    protected Map<String, Boolean> result;

    public MigrationExecutor(EmsAdminHandler emsHandler ) {
        this.emsHandler = emsHandler;
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */
    public Map<String, Boolean> execute(List<String> migrations, List<String> rollbacks, Map<String, String> options) throws MigrationException {
        result = new LinkedHashMap<>();

        for (String m : migrations) {
            boolean success = emsHandler.execute(m, null);
            result.put(new File(m).getName(), success);

            if (!success) {
                try {
                    onMigrationFailure(migrations, rollbacks, m, options);
                } catch (Exception e) {
                    break;
                }
            }
        }

        return result;
    }

    public static MigrationExecutor create(Class<? extends MigrationExecutor> strategy, EmsAdminHandler emsHandler) throws MigrationException{
        MigrationExecutor impl;
        try {
            Constructor<? extends MigrationExecutor> c = strategy.getConstructor(EmsAdminHandler.class);
            impl = c.newInstance(emsHandler);
        } catch (Exception e) {
            throw new MigrationException("Cannot create MigrationExecutor", e);
        }

        return impl;
    }

    public int getLastSuccessfulUpMigrationNumber(int initialValue) {
        return extractLargestSuccessfulMigrationNumber(result, initialValue);
    }

    public int getLastSuccessfulDownMigrationNumber(int initialValue) {
        return extractLowestSuccessfulMigrationNumber(result, initialValue);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */
    protected abstract void onMigrationFailure(List<String> migrations, List<String> rollbacks, String failedMigration, Map<String, String> options) throws MigrationException;


}
