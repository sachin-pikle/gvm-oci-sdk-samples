/**
 * Copyright (c) 2016, 2023, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.gvm.samples;

import java.util.List;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.InstancePrincipalsAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;


public class ObjectStorageSyncExample {

    // These two constants are needed to configure the AuthenticationDetailsProvider. Assumes there is:
    // - a default OCI config file "~/.oci/config", and 
    // - a profile in that config with the name "DEFAULT". 
    // Make changes to the following, if needed.
    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "DEFAULT";

    /**
     * This example covers the GetNamespace operation across tenants. Additional permissions will be
     * required to perform more Object Storage operations.
     *
     */    
    public static String getOSNamespace(String compartmentId) {

        String namespace = null;
        ConfigFileReader.ConfigFile configFile = null;

        // Option 1 - When running on local
        try {
            configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
        } catch (java.io.IOException ioe) {
            System.err.println("ioe.getMessage() " + ioe.getMessage());
            ioe.printStackTrace();
            return namespace;
        }
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        // Option 2 - When running in an OCI Instance 
        // final InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        // Option 3 - When runnning in OCI Functions
        // final ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
            
        try (ObjectStorageClient objectStorageClient = ObjectStorageClient.builder().build(provider)) {
            
            // Construct GetNamespaceRequest with the given compartmentId.
            GetNamespaceRequest getNamespaceRequest = GetNamespaceRequest.builder().compartmentId(compartmentId).build();
            namespace = objectStorageClient.getNamespace(getNamespaceRequest).getValue();

            System.out.println(
                    String.format(
                            "Object Storage namespace for compartment [%s] is [%s]",
                            compartmentId, namespace));
            
                    
        } catch (Exception e) {
            System.err.println("e.getMessage() " + e.getMessage());
            e.printStackTrace();
        }

        return namespace;

    }

    /**
     * List buckets in a given compartment.
     *
     */    
    // public static List<String> listBuckets(String compId) {

    //     List<String> bucketIds = null;
    //     try (
    //         // Option 1 - When running on local
    //         final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
    //         final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
    //         // Option 2 - When running in an OCI Instance 
    //         // final InstancePrincipalsAuthenticationDetailsProvider provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
    //         // Option 3 - When runnning in OCI Functions
    //         // final ResourcePrincipalAuthenticationDetailsProvider provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
            
    //         ObjectStorageClient objectStorageClient = ObjectStorageClient.builder().build(provider)) {
            
    //         // ListInstancesRequest request = ListInstancesRequest.builder()
    //         //     .compartmentId(compId)
    //         //     .build();

    //         // ListInstancesResponse instances = computeClient.listInstances(request);
    //         // List<Instance> instanceList = instances.getItems();
    //         // System.out.println("No. of compute instances found: " + instanceList.size());
            
    //         // // Add all instance ids to a List
    //         // instanceIds = instanceList.stream()
    //         //     .map(Instance::getId)
    //         //     .collect(Collectors.toList());

    //         // Construct GetNamespaceRequest with the given compartmentId.
    //         GetNamespaceRequest getNamespaceRequest =
    //                 GetNamespaceRequest.builder().compartmentId(compartmentId).build();
    //         String namespace = objectStorageClient.getNamespace(getNamespaceRequest).getValue();

    //         System.out.println(
    //                 String.format(
    //                         "Object Storage namespace for compartment [%s] is [%s]",
    //                         compartmentId, namespace));
                    
                    
    //     } catch (Exception e) {
    //         System.err.println("e.getMessage() " + e.getMessage());
    //         e.printStackTrace();
    //     }

    //     System.out.println("Compute instance ids: " + instanceIds);       
    //     return instanceIds;
    // }
}
