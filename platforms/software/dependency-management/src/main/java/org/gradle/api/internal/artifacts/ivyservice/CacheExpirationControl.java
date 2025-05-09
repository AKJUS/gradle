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

package org.gradle.api.internal.artifacts.ivyservice;

import org.gradle.api.artifacts.ModuleIdentifier;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.ResolvedModuleVersion;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.internal.component.external.model.ModuleComponentArtifactMetadata;

import java.io.File;
import java.time.Duration;
import java.util.Set;

/**
 * Determines whether cached external artifacts and metadata should be considered expired.
 */
public interface CacheExpirationControl {

    Expiry versionListExpiry(ModuleIdentifier moduleIdentifier, Set<ModuleVersionIdentifier> moduleVersions, Duration age);

    Expiry missingModuleExpiry(ModuleComponentIdentifier component, Duration age);

    Expiry moduleExpiry(ModuleComponentIdentifier component, ResolvedModuleVersion resolvedModuleVersion, Duration age);

    Expiry moduleExpiry(ResolvedModuleVersion resolvedModuleVersion, Duration age, boolean changing);

    Expiry moduleArtifactsExpiry(
        ModuleVersionIdentifier moduleVersionId, Set<ModuleComponentArtifactMetadata> artifacts,
        Duration age, boolean belongsToChangingModule, boolean moduleDescriptorInSync
    );

    Expiry artifactExpiry(ModuleComponentArtifactMetadata artifactMetadata, File cachedArtifactFile, Duration age, boolean belongsToChangingModule, boolean moduleDescriptorInSync);

    Expiry changingModuleExpiry(ModuleComponentIdentifier component, ResolvedModuleVersion resolvedModuleVersion, Duration age);

    interface Expiry {

        boolean isMustCheck();

        Duration getKeepFor();

    }
}
