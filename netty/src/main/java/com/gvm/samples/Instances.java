/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gvm.samples;

import java.util.List;
import java.util.stream.Collectors;

import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.responses.ListInstancesResponse;


public class Instances {
    private static final String PROFILE_DEFAULT = "DEFAULT";


    /**
     * List instances in a given compartment.
     *
     */    
    public static List<String> listInstances(String compId) {

        List<String> instanceIds = null;
        AuthenticationDetailsProvider provider = null;

        try {
            // When running on local
            provider = new ConfigFileAuthenticationDetailsProvider(PROFILE_DEFAULT);
            // When running in an OCI Instance 
            // InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
            // When runnning in OCI Functions
            // ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
            
        } catch (java.io.IOException ioe) {
            System.err.println("ioe.getMessage() " + ioe.getMessage());
            ioe.printStackTrace();
        }
        
        try (ComputeClient computeClient = ComputeClient.builder().build(provider)){
            ListInstancesRequest request = ListInstancesRequest.builder()
                    // .compartmentId("ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba")
                    .compartmentId(compId)
                    .build();

            ListInstancesResponse instances = computeClient.listInstances(request);
            List<Instance> instanceList = instances.getItems();
            System.out.println("No. of compute instances found: " + instanceList.size());
            
            // Accumulate ids into a List
            instanceIds = instanceList.stream()
                .map(Instance::getId)
                .collect(Collectors.toList());

        }
            
        System.out.println("Compute instance ids: " + instanceIds);       
        return instanceIds;
    }
}
