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
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.responses.ListInstancesResponse;


public class ComputeInstancesExample {

    // This boolean flag is used to toggle between Config File and Instance Principal Authentication.
    // Set it to 'false' (default) to use Config File Authentication when running on local.
    // Set it to 'true' to use Instance Principal Authentication when running in an OCI Instance.
    private static final boolean IP_AUTH = false;

    // These two constants are needed to configure the ConfigFileAuthenticationDetailsProvider.
    // Assumes there is:
    // - a default OCI config file "~/.oci/config", and
    // - a profile in that config with the name "DEFAULT".
    // Make changes to the following, if needed.
    private static final String CONFIG_LOCATION = "~/.oci/config";
    private static final String CONFIG_PROFILE = "DEFAULT";

    /**
     * List instances in a given compartment.
     *
     */
    public static List<String> listInstances(String compartmentId) {

        List<String> instanceIds = null;
        ConfigFileReader.ConfigFile configFile = null;
        BasicAuthenticationDetailsProvider provider = null;

        if (IP_AUTH) {
            // Option 1: Instance Principal Authentication - Use when running in an OCI Instance
            provider = InstancePrincipalsAuthenticationDetailsProvider.builder().build();
        } else {
            // Option 2: Config File Authentication - Use when running on local
            try {
                configFile = ConfigFileReader.parse(CONFIG_LOCATION, CONFIG_PROFILE);
            } catch (java.io.IOException ioe) {
                System.err.println("ioe.getMessage() " + ioe.getMessage());
                // ioe.printStackTrace();
                return instanceIds;
            }
            provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        }

        // Option 3 - When running in OCI Functions
        // provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();

        try (ComputeClient client = ComputeClient.builder().build(provider)) {
            ListInstancesRequest request = ListInstancesRequest.builder()
                    .compartmentId(compartmentId)
                    .build();

            ListInstancesResponse listInstancesResponse = client.listInstances(request);
            List<Instance> instanceList = listInstancesResponse.getItems();
            System.out.println("No. of compute instances found: " + instanceList.size());

            // Add all instance ids to a List
            instanceIds = instanceList.stream()
                .map(Instance::getId)
                .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("e.getMessage() " + e.getMessage());
            // e.printStackTrace();
        }

        System.out.println("Compute instance ids: " + instanceIds);
        return instanceIds;
    }
}
