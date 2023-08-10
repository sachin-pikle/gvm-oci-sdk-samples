/**
 * Copyright (c) 2016, 2023, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
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
            // Option 1 - When running on local
            provider = new ConfigFileAuthenticationDetailsProvider(PROFILE_DEFAULT);
            // Option 2 - When running in an OCI Instance 
            // InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
            // Option 3 - When runnning in OCI Functions
            // ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
            
        } catch (java.io.IOException ioe) {
            System.err.println("ioe.getMessage() " + ioe.getMessage());
            ioe.printStackTrace();
        }
        
        try (ComputeClient computeClient = ComputeClient.builder().build(provider)){
            ListInstancesRequest request = ListInstancesRequest.builder()
                    .compartmentId(compId)
                    .build();

            ListInstancesResponse instances = computeClient.listInstances(request);
            List<Instance> instanceList = instances.getItems();
            System.out.println("No. of compute instances found: " + instanceList.size());
            
            // Add all instance ids to a List
            instanceIds = instanceList.stream()
                .map(Instance::getId)
                .collect(Collectors.toList());

        }

        System.out.println("Compute instance ids: " + instanceIds);       
        return instanceIds;
    }
}
