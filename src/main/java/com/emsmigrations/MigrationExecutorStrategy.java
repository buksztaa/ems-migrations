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

import java.util.Arrays;
import java.util.List;

/**
 * Created by adambuksztaler on 05/08/16.
 */
public enum MigrationExecutorStrategy {

        IGNORE("ignore", MigrationExecutorIgnoreStrategy.class),
        ROLLBACK("rollback", MigrationExecutorRollbackStrategy.class),
        TERMINATE("terminate", MigrationExecutorTerminateStrategy.class);

        public final String value;
        public final Class<? extends MigrationExecutor> impl;

        public static final List<MigrationExecutorStrategy> enumerations = Arrays.asList(MigrationExecutorStrategy.values());

        MigrationExecutorStrategy(String value, Class<? extends MigrationExecutor> impl) {
            this.value = value;
            this.impl = impl;
        }

        public static MigrationExecutorStrategy getByValue(String value) {
            return enumerations.stream()
                    .filter(e -> (e.value == value))
                    .findFirst()
                    .get();
        }

}
