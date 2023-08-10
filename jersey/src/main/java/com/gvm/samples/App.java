/**
 * Copyright (c) 2016, 2023, Oracle and/or its affiliates.  All rights reserved.
 * This software is dual-licensed to you under the Universal Permissive License (UPL) 1.0 as shown at https://oss.oracle.com/licenses/upl or Apache License 2.0 as shown at http://www.apache.org/licenses/LICENSE-2.0. You may choose either license.
 */
package com.gvm.samples;

import java.util.List;

public class App {

    public static void main( String[] args ) throws Exception {

        System.out.println("********** In App.java - Before calling listInstances() **********");       
        List<String> instanceIds = Instances.listInstances(args[0]);
        System.out.println("********** In App.java - After calling listInstances() **********");       
    }
}
