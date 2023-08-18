/*
** Copyright (c) 2020, 2023 Oracle, Inc. All rights reserved.
** Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/
package com.example.fn;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.responses.ListInstancesResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ComputeInstancesList {

    final ResourcePrincipalAuthenticationDetailsProvider provider
            = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
    
    public List<String> handle(String compartmentId){

        List<String> instanceIds = new ArrayList<String>();

        try (ComputeClient client = ComputeClient.builder().build(provider)) {
            ListInstancesRequest request = ListInstancesRequest.builder()
                    .compartmentId(compartmentId)
                    .build();

            ListInstancesResponse listInstancesResponse = client.listInstances(request);
            List<Instance> instanceList = listInstancesResponse.getItems();
            System.out.println("No. of compute instances found: " + instanceList.size());
        
            // Add all instance ids to a List
            instanceIds.addAll(instanceList.stream()
                .map(Instance::getId)
                .collect(Collectors.toList()));

        } catch (Exception e) {
            System.err.println("e.getMessage() " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Compute instance ids: " + instanceIds);       
        return instanceIds;
    }
}
