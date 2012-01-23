/*
 * Copyright 2011 the original author or authors.                          
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

package org.gradle.logging.internal;

import org.gradle.api.internal.Operation;
import org.gradle.api.internal.concurrent.Synchronizer;
import org.gradle.api.logging.LogLevel;

/**
 * by Szczepan Faber, created at: 1/23/12
 */
public class SingleOpLoggingConfigurer implements LoggingConfigurer {

    private final Synchronizer sync = new Synchronizer();
    private boolean configured;
    final LoggingConfigurer delegate;

    public SingleOpLoggingConfigurer(LoggingConfigurer delegate) {
        this.delegate = delegate;
    }

    public void configure(final LogLevel logLevel) {
        if (configured) {
            return;
        }
        sync.synchronize(new Operation() {
            public void execute() {
                if (configured) {
                    return;
                }
                configured = true;
                //assume the configuration does not fail. If it fails we don't want to re-run it anyway.
                delegate.configure(logLevel);
            }
        });
    }
}
