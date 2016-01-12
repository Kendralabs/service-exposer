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
package org.trustedanalytics.serviceexposer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.trustedanalytics.serviceexposer.cloud.CredentialProperties;
import org.trustedanalytics.serviceexposer.cloud.CredentialsStore;
import org.trustedanalytics.serviceexposer.nats.registrator.NatsMessagingQueue;
import org.trustedanalytics.serviceexposer.nats.registrator.RegistratorJob;

import java.util.List;
import java.util.Vector;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ToolsRegistratorJobTests {


    private RegistratorJob sut;

    private static final String SERVICE_TYPE_RSTUDIO = "rstudio";
    private static final String SERVICE_TYPE_IPYTHON= "ipython";

    @Mock
    private NatsMessagingQueue natsOps;

    @Mock
    private CredentialsStore store;

    @Mock
    private CredentialProperties hueCredentials;

    private List<String> serviceTypes;

    private CredentialProperties hueEntry;

    private CredentialProperties arcadiaEntry;

    @Before
    public void setup() {
        serviceTypes = ImmutableList.of(SERVICE_TYPE_RSTUDIO, SERVICE_TYPE_IPYTHON);
        hueEntry = new CredentialProperties("","","","hueInstance","","","","","");
        arcadiaEntry = new CredentialProperties("","","","arcadiaInstance","","","","","");
        sut = new RegistratorJob(natsOps, store,serviceTypes,ImmutableList.of(hueEntry,arcadiaEntry));
    }

    @Test
    public void testRegistratorJobRunMethodForHueService(){
        when(store.getAllCredentialsEntries(SERVICE_TYPE_IPYTHON)).thenReturn(new Vector<CredentialProperties>());
        when(store.getAllCredentialsEntries(SERVICE_TYPE_RSTUDIO)).thenReturn(new Vector<CredentialProperties>());
        sut.run();
        verify(natsOps).registerPathInGoRouter(hueEntry);
    }

    @Test
    public void testRegistratorJobRunMethodForArcadiaService(){
        when(store.getAllCredentialsEntries(SERVICE_TYPE_IPYTHON)).thenReturn(new Vector<CredentialProperties>());
        when(store.getAllCredentialsEntries(SERVICE_TYPE_RSTUDIO)).thenReturn(new Vector<CredentialProperties>());
        sut.run();
        verify(natsOps).registerPathInGoRouter(arcadiaEntry);
    }
}