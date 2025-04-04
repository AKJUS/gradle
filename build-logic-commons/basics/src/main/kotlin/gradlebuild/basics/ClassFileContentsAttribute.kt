/*
 * Copyright 2020 the original author or authors.
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

package gradlebuild.basics

import org.gradle.api.attributes.Attribute

enum class ClassFileContentsAttribute {
    /**
     * Only include method declarations with stub code; eliminate private members.
     */
    STUBS,

    /**
     * Include the complete bytecode complete with method bodies and private members.
     */
    COMPLETE;

    companion object {
        val attribute = Attribute.of(ClassFileContentsAttribute::class.java)
    }
}
