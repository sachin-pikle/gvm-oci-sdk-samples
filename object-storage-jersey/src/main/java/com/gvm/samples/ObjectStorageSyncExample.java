/**
 * Copyright (c) 2016, 2023, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.gvm.samples;

import java.util.List;
import java.util.stream.Collectors;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;

public class ObjectStorageSyncExample {

    // These two constants are needed to configure the
    // AuthenticationDetailsProvider. Assumes there is:
    // - a default OCI config file "~/.oci/config", and
    // - a profile in that config with the name "DEFAULT".
    // Make changes to the following, if needed.
    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "DEFAULT";

    /**
     * List buckets in a given compartment.
     *
     */
    public static List<String> listBuckets(String compartmentId) {

        List<String> bucketNames = null;
        ConfigFileReader.ConfigFile configFile = null;
        BasicAuthenticationDetailsProvider provider = null;

        // Option 1 - When running on local
        try {
            configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
        } catch (java.io.IOException ioe) {
            System.err.println("ioe.getMessage() " + ioe.getMessage());
            // ioe.printStackTrace();
            return bucketNames;
        }
        provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        // Option 2 - When running in an OCI Instance
        // provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        // Option 3 - When runnning in OCI Functions
        // provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();

        try (ObjectStorageClient client = ObjectStorageClient.builder().build(provider)) {

            // Construct GetNamespaceRequest with the given compartmentId.
            GetNamespaceRequest getNamespaceRequest = GetNamespaceRequest.builder().compartmentId(compartmentId)
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
            bucketNames = bucketList.stream()
                    .map(BucketSummary::getName)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("e.getMessage() " + e.getMessage());
            // e.printStackTrace();
        }

        System.out.println("Object Storage bucket names: " + bucketNames);
        return bucketNames;
    }
}
