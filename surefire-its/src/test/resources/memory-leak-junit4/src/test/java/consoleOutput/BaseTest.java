/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consoleOutput;

import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import io.github.checkleak.core.CheckLeak;

import org.junit.Assert;

public class BaseTest {

    public static boolean waitFor(long timeout, int sleep, BooleanSupplier clause) {

        try {
            for (long expire = System.currentTimeMillis() + timeout; System.currentTimeMillis() < expire; ) {
                if (clause.getAsBoolean()) {
                    return true;
                }
                TimeUnit.MILLISECONDS.sleep(sleep);
            }
            return clause.getAsBoolean();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void validateLeaks() throws Exception {
        assertMemory(1, 15, 30, "org.apache.maven.surefire.api.util.internal.ClassMethod");
    }

    public static void assertMemory(int maxExpected, int maxLevel, int maxObjects, String clazz) throws Exception {
        CheckLeak checkLeak = new CheckLeak();
        waitFor(5000, 100, () -> checkLeak.getAllObjects(clazz).length <= maxExpected);

        Object[] objects = checkLeak.getAllObjects(clazz);
        if (objects.length > maxExpected) {
            for (Object obj : objects) {
                System.out.println("Object " + obj + " still in the heap");
            }
            String report = checkLeak.exploreObjectReferences(maxLevel, maxObjects, true, objects);
            System.out.println(report);

            Assert.fail("Class " + clazz + " has leaked " + objects.length + " objects\n" + report);
        }
    }

}
