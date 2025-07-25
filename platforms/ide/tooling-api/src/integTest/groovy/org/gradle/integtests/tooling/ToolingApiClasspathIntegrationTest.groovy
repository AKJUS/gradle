/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.integtests.tooling

import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.RepoScriptBlockUtil
import org.gradle.integtests.fixtures.executer.IntegrationTestBuildContext
import org.gradle.util.internal.TextUtil

class ToolingApiClasspathIntegrationTest extends AbstractIntegrationSpec {

    def "tooling api classpath contains only tooling-api jar and slf4j"() {
        IntegrationTestBuildContext buildContext = new IntegrationTestBuildContext()
        buildFile << """
            plugins {
                id("java-library")
            }

            // For the current tooling API jar
            repositories {
                maven {
                    url = uri(file("${TextUtil.escapeString(buildContext.localRepository)}"))
                }
            }

            // For SFL4J
            repositories {
                ${RepoScriptBlockUtil.gradleRepositoryDefinition()}
            }

            dependencies {
                implementation("org.gradle:gradle-tooling-api:${distribution.getVersion().baseVersion.version}")
            }

            tasks.register("resolve") {
                def files = configurations.runtimeClasspath.incoming.files
                doLast {
                    // If either of these expectations change,
                    // `ToolingApiDistributionResolver` must be updated.
                    assert files.size() == 2
                    assert files.find { it.name ==~ /slf4j-api-.*\\.jar/ } != null

                    // If this suddenly fails without an obvious reason, you likely have added some code
                    // that references types that were previously eliminated from gradle-tooling-api.jar.
                    def jarSize = files.find { it.name ==~ /gradle-tooling-api.*\\.jar/ }.size()
                    assert jarSize < 3_300_000
                    // If this suddenly fails you should adjust both values so the size reduction doesn't accidentally regress again.
                    assert jarSize > 3_000_000
                }
            }
        """

        expect:
        succeeds("resolve")
    }
}
