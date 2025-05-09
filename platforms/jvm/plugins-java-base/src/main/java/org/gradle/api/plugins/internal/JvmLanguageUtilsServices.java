/*
 * Copyright 2024 the original author or authors.
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

package org.gradle.api.plugins.internal;

import org.gradle.api.plugins.jvm.internal.DefaultJvmLanguageUtilities;
import org.gradle.api.plugins.jvm.internal.JvmLanguageUtilities;
import org.gradle.internal.service.ServiceRegistration;
import org.gradle.internal.service.scopes.AbstractGradleModuleServices;

/**
 * Registers services containing utilities used by projects that compile JVM language source.
 */
public class JvmLanguageUtilsServices extends AbstractGradleModuleServices {
    @Override
    public void registerProjectServices(ServiceRegistration registration) {
        registration.add(JvmLanguageUtilities.class, DefaultJvmLanguageUtilities.class);
    }
}
