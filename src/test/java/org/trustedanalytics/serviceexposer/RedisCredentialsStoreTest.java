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
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.trustedanalytics.serviceexposer.cloud.CredentialProperties;
import org.trustedanalytics.serviceexposer.cloud.CredentialsStore;
import org.trustedanalytics.serviceexposer.cloud.RedisCredentialsStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RedisCredentialsStoreTest {

    private static final String SERVICE_TYPE = "rstudio";

    private CredentialsStore sut;

    @Mock
    private RedisOperations<String, CredentialProperties> template;

    @Mock
    private CredentialProperties mockCredentialsProperties;

    @Mock
    private HashOperations<String, Object, Object> mockHashOps;

    @Before
    public void setUp() {
        when(template.opsForHash()).thenReturn(mockHashOps);
        sut = new RedisCredentialsStore(template);
    }

    @Test
    public void testGetSurplusKeys() {
        Set<Object> redisGuids = Sets.newHashSet("1", "2", "3", "4");
        Set<String> retrievedGuids = Sets.newHashSet("1", "2");

        when(mockHashOps.keys(SERVICE_TYPE)).thenReturn(redisGuids);
        Set<String>  keysToDelete = sut.getSurplusServicesGUIDs(SERVICE_TYPE, retrievedGuids);
        boolean eligible = keysToDelete.containsAll(Arrays.asList("3", "4"));
        assertEquals(true, eligible);
    }

    @Test
    public void testServiceInstanceExists() {
        UUID randomGUID = UUID.randomUUID();
        CredentialProperties existingEntry = new CredentialProperties("",randomGUID.toString(),"","","","","","","");
        when(mockHashOps.get(SERVICE_TYPE, randomGUID.toString())).thenReturn(existingEntry);
        boolean eligible = sut.exists(SERVICE_TYPE, randomGUID);
        assertEquals(true, eligible);
    }

    @Test
    public void testGetCredentialsInJSON() {
        String serviceName = "tested";
        String randomServiceGUID = UUID.randomUUID().toString();
        UUID randomSpaceGUID = UUID.randomUUID();
        UUID randomSpaceGUID2 = UUID.randomUUID();

        Map<String, String> entry = ImmutableMap.of("guid", randomServiceGUID, "hostname", serviceName+"-"+randomServiceGUID, "login", "","password","");

        when(mockCredentialsProperties.retriveMapForm()).thenReturn(entry);
        when(mockCredentialsProperties.getSpaceGuid()).thenReturn(randomSpaceGUID.toString());

        CredentialProperties existingEntry = new CredentialProperties("",randomServiceGUID,randomSpaceGUID.toString(),serviceName,"","","","","");

        List<Object> serviceEntries = ImmutableList.of(existingEntry);
        when(mockHashOps.values(SERVICE_TYPE)).thenReturn(serviceEntries);
        Map<String, Map<String, String>> jsonMap = sut.getCredentialsInJSON(SERVICE_TYPE, randomSpaceGUID);

        assertEquals(ImmutableMap.of(serviceName, entry), jsonMap);
    }
}