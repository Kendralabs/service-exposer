/**
 * Copyright (c) 2015 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trustedanalytics.serviceexposer.nats.registrator;

import org.trustedanalytics.serviceexposer.keyvaluestore.CredentialProperties;
import org.trustedanalytics.serviceexposer.keyvaluestore.CredentialsStore;
import org.trustedanalytics.serviceexposer.queue.MessagingQueue;

import java.util.List;

public class RegistratorJob {

    private MessagingQueue natsOps;
    private CredentialsStore<CredentialProperties> store;
    private List<String> serviceTypes;
    private List<CredentialProperties> externalTools;

    public RegistratorJob(MessagingQueue natsOps, CredentialsStore<CredentialProperties> store, List<String> serviceTypes, List<CredentialProperties> toolsCredentials) {
        this.natsOps = natsOps;
        this.store = store;
        this.serviceTypes = serviceTypes;
        this.externalTools = toolsCredentials;
    }

    public void run() {
        for (CredentialProperties entry : externalTools) {
            natsOps.registerPathInGoRouter(entry);
        }

        for (String serviceType : serviceTypes) {
            for (CredentialProperties entry : store.values(serviceType)) {
                natsOps.registerPathInGoRouter(entry);
            }
        }
    }
}
