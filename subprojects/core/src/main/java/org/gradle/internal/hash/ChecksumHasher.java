/*
 * Copyright 2019 the original author or authors.
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
package org.gradle.internal.hash;

import org.gradle.internal.UncheckedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class ChecksumHasher implements FileHasher {

    private final HashFunction hashFunction;

    public ChecksumHasher(HashFunction hashFunction) {
        this.hashFunction = hashFunction;
    }

    @Override
    public HashCode hash(File file) {
        try {
            PrimitiveHasher hasher = hashFunction.newPrimitiveHasher();
            byte[] buffer = new byte[4096];
            int len;
            try (InputStream in = new FileInputStream(file)) {
                while ((len = in.read(buffer)) >= 0) {
                    hasher.putBytes(buffer, 0, len);
                }
            }
            return hasher.hash();
        } catch (IOException e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }

    @Override
    public HashCode hash(File file, long length, long lastModified) {
        return hash(file);
    }

}
