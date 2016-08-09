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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by adambuksztaler on 05/08/16.
 */
public class MigrationExecutorRollbackStrategy extends MigrationExecutor {
    public MigrationExecutorRollbackStrategy(EmsAdminHandler emsHandler) {
        super(emsHandler);
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        INTERFACE
    --------------------------------------------------------------------------------------------------------------------
     */
    @Override
    protected void onMigrationFailure(List<String> migrations, List<String> rollbacks, String failedMigration, Map<String, String> options) throws MigrationException {
        rollbackAfterFailure(rollbacks, failedMigration, options);
    }

    @Override
    public int getLastSuccessfulUpMigrationNumber(int initialValue) {
        return initialValue;
    }

    @Override
    public int getLastSuccessfulDownMigrationNumber(int initialValue) {
        return initialValue;
    }

    /*
    --------------------------------------------------------------------------------------------------------------------
        PRIVATE SECTION
    --------------------------------------------------------------------------------------------------------------------
     */

    private void rollbackAfterFailure(List<String> rollbacks, String failedMigration, Map<String, String> options) throws MigrationException {
        List<String> rollbacksToRun = sublistRollbacks(rollbacks, failedMigration);

        MigrationExecutor ignoreExec = MigrationExecutor.create(MigrationExecutorIgnoreStrategy.class, emsHandler);
        System.out.println("Rollbacks to run:");
        rollbacksToRun.forEach(r -> System.out.println(r));
        if (rollbacksToRun != null && rollbacksToRun.size() > 0) {
            ignoreExec.execute(rollbacksToRun, null, options);
            System.out.println("Rollbacks executed");
        }

        throw new MigrationException("Terminated after rollback");
    }

    private List<String> sublistRollbacks(List<String> rollbacks, String failedMigration) {
        int failedMigrationNumber = extractMigrationNumber(failedMigration);
        String rollbackToBeginWith = rollbacks.stream()
                .filter(r -> (extractMigrationNumber(r) == failedMigrationNumber))
                .findFirst()
                .get();
        return sublistAfter(rollbacks, rollbackToBeginWith);
    }

}
