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
package org.apache.maven.surefire.its.leaktests;

import java.util.HashMap;
import java.util.Map;

import org.apache.maven.surefire.its.fixture.MavenLauncher;
import org.apache.maven.surefire.its.fixture.OutputValidator;
import org.apache.maven.surefire.its.fixture.SurefireJUnit4IntegrationTestCase;
import org.junit.Test;

public class AbstractLeakTest extends SurefireJUnit4IntegrationTestCase {

    private static final int NUMBER_OF_TESTS = 50;

    static Map<String, String> props(int forkCount, int skipAfterFailureCount, boolean reuseForks) {
        Map<String, String> props = new HashMap<>(3);
        props.put("surefire.skipAfterFailureCount", "" + skipAfterFailureCount);
        props.put("forkCount", "" + forkCount);
        props.put("reuseForks", "" + reuseForks);
        return props;
    }

    protected String withProvider() {
        return "junit";
    }

    private OutputValidator prepare() {
        MavenLauncher launcher = unpack("/fail-fast-" + withProvider()).maven().debugLogging();

        return launcher.executeTest();
    }

    @Test
    public void test() throws Exception {
        prepare().assertTestSuiteResults(NUMBER_OF_TESTS, 0, 0, 0);
    }
}
