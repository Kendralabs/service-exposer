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

package org.trustedanalytics.serviceexposer.queue;

import nats.client.Nats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trustedanalytics.serviceexposer.keyvaluestore.CredentialProperties;

public class NatsMessagingQueue implements MessagingQueue {

    private static final Logger LOG = LoggerFactory.getLogger(NatsMessagingQueue.class);

    private static final String NATS_ROUTE_REGISTER = "router.register";
    private static final String NATS_ROUTE_UNREGISTER = "router.unregister";

    private Nats nats;

    public NatsMessagingQueue(Nats nats) {
        this.nats = nats;
    }

    @Override
    public void registerPathInGoRouter(CredentialProperties serviceInfo) {
        if(serviceInfo.isCredentialsExtracted()){
            nats.publish(NATS_ROUTE_REGISTER, serviceInfo.toString());
            LOG.info("Route registered: {}:{} -> {}",
                    serviceInfo.getIpAddress(),
                    serviceInfo.getPort(),
                    serviceInfo.getHostName()
            );
        }else{
            LOG.info("Failed to register route for instance: {}", serviceInfo.getServiceInstaceGuid());
        }
    }

    @Override
    public void unregisterPathInGoRouter(CredentialProperties serviceInfo) {
        if(serviceInfo.isCredentialsExtracted()){
            nats.publish(NATS_ROUTE_UNREGISTER, serviceInfo.toString());
            LOG.info("Route unregistered: {} -> {}:{}",
                    serviceInfo.getHostName(),
                    serviceInfo.getIpAddress(),
                    serviceInfo.getPort()
            );
        }
    }
}
