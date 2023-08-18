/*
** Copyright (c) 2020, 2023 Oracle, Inc. All rights reserved.
** Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
*/
package com.example.fn;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectStorageBucketsList {

    final ResourcePrincipalAuthenticationDetailsProvider provider
            = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
    
    public List<String> handle(String compartmentId){

        List<String> bucketNames = new ArrayList<String>();

        try (ObjectStorageClient client = ObjectStorageClient.builder().build(provider)) {

            // Construct GetNamespaceRequest with the given compartmentId.
            GetNamespaceRequest getNamespaceRequest = GetNamespaceRequest.builder()
                    .compartmentId(compartmentId)
                    .build();
            String namespace = client.getNamespace(getNamespaceRequest).getValue();

            System.out.println(
                    String.format(
                            "Object Storage namespace for compartment [%s] is [%s]",
                            compartmentId, namespace));

            Builder listBucketsBuilder = ListBucketsRequest.builder()
                    .namespaceName(namespace)
                    .compartmentId(compartmentId);

            ListBucketsResponse listBucketsResponse = client.listBuckets(listBucketsBuilder.build());

            List<BucketSummary> bucketList = listBucketsResponse.getItems();
            System.out.println("No. of Object Storage buckets found: " + bucketList.size());

            // Add all bucket names to a List
            bucketNames.addAll(bucketList.stream()
                    .map(BucketSummary::getName)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            System.err.println("e.getMessage() " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Object Storage bucket names: " + bucketNames);
        return bucketNames;
    }
}
