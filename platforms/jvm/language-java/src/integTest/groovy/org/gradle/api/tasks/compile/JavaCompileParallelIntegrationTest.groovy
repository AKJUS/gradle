/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.api.tasks.compile

import com.google.common.collect.Iterables
import org.gradle.integtests.fixtures.AbstractIntegrationSpec
import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.internal.jvm.Jvm
import org.gradle.test.precondition.Requires
import org.gradle.test.preconditions.IntegTestPreconditions
import org.gradle.util.internal.TextUtil
import org.junit.Assume
import spock.lang.Issue

@Requires([IntegTestPreconditions.NotParallelExecutor])
class JavaCompileParallelIntegrationTest extends AbstractIntegrationSpec {

    @Issue("https://issues.gradle.org/browse/GRADLE-3029")
    def "system property java.home is not modified across compile task boundaries"() {
        def projectNames = ['a', 'b', 'c', 'd', 'e', 'f', 'g']

        List<Jvm> java8CompatibleJdks = AvailableJavaHomes.getAvailableJdks { it.getJavaMajorVersion() >= 8 }
        Assume.assumeTrue(java8CompatibleJdks.size() >= 2)
        def jdks = Iterables.cycle(java8CompatibleJdks).iterator()

        settingsFile << "include ${projectNames.collect { "'$it'" }.join(', ')}"
        buildFile << """
            subprojects {
                apply plugin: 'java'

                ${mavenCentralRepository()}

                dependencies {
                    implementation 'commons-lang:commons-lang:2.5'
                }
            }
        """

        projectNames.each { projectName ->
            def jdk = jdks.next()
            def javaHome = TextUtil.escapeString(jdk.javaHome.absolutePath)
            def version = jdk.javaVersion
            buildFile << """
project(':$projectName') {
    tasks.withType(JavaCompile) {
        sourceCompatibility = '${version}'
        targetCompatibility = '${version}'

        options.with {
            fork = true
            forkOptions.javaHome = file("${javaHome}")
        }
    }
}
"""
            file("${projectName}/src/main/java/Foo.java") << """
import org.apache.commons.lang.StringUtils;

public class Foo {
    public String capitalize(String str) {
        return StringUtils.capitalize(str);
    }
}
"""
        }

        when:
        args('--parallel')
        args('--max-workers=4')
        run('compileJava')

        then:
        noExceptionThrown()

        projectNames.each { projectName ->
            file("${projectName}/build/classes/main/Foo.class").exists()
        }
    }

}
