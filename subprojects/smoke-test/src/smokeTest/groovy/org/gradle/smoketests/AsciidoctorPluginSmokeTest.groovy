/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.smoketests

import org.gradle.integtests.fixtures.ToBeFixedForConfigurationCache
import spock.lang.Issue
import spock.lang.Unroll

import static org.gradle.internal.reflect.TypeValidationContext.Severity.WARNING

class AsciidoctorPluginSmokeTest extends AbstractPluginValidatingSmokeTest {

    @Issue('https://github.com/asciidoctor/asciidoctor-gradle-plugin/releases')
    @Unroll
    @ToBeFixedForConfigurationCache(because = "Task.getProject() during execution")
    def 'asciidoctor plugin #version'() {
        given:
        buildFile << """
            plugins {
                id 'org.asciidoctor.jvm.convert' version '${version}'
            }

            repositories {
                ${jcenterRepository()}
            }
        """

        file('src/docs/asciidoc/test.adoc') << """
            = Line Break Doc Title
            :hardbreaks:

            Rubies are red,
            Topazes are blue.
            """.stripIndent()

        when:
        runner('asciidoc').build()

        then:
        file('build/docs/asciidoc').isDirectory()

        where:
        version << TestedVersions.asciidoctor
    }

    @Override
    Map<String, Versions> getPluginsToValidate() {
        TestedVersions.asciidoctor.collectEntries([:]) {
            ["org.asciidoctor.jvm.convert", Versions.of(it)]
        }
    }

    @Override
    void configureValidation(String pluginId, String version) {
        validatePlugins {
            onPlugin(pluginId) {
                passes()
            }

            onPlugin('org_asciidoctor_gradle_base_AsciidoctorBasePlugin') {
                failsWith([
                    "Type 'AbstractAsciidoctorBaseTask': field 'configuredOutputOptions' without corresponding getter has been annotated with @Nested.": WARNING,
                    "Type 'AbstractAsciidoctorBaseTask': non-property method 'attributes()' should not be annotated with: @Input.": WARNING,
                    "Type 'AbstractAsciidoctorBaseTask': non-property method 'getDefaultResourceCopySpec()' should not be annotated with: @Internal.": WARNING,
                    "Type 'AbstractAsciidoctorBaseTask': non-property method 'getResourceCopySpec()' should not be annotated with: @Internal.": WARNING,
                    "Type 'SlidesToExportAware': property 'profile' is not annotated with an input or output annotation.": WARNING
                ])
            }

            onPlugin('org.asciidoctor.gradle.jvm.AsciidoctorJBasePlugin') {
                passes()
            }
        }
    }
}
