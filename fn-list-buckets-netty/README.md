# Netty-based GraalVM Native Image Function that returns a list of buckets in a given compartment

This function uses Resource Principals to securely authorize a function to make
API calls to OCI services using the [OCI Java SDK](https://docs.cloud.oracle.com/iaas/tools/java/latest/).
It returns a list of all buckets within a given compartment.

The function calls the following OCI Java SDK classes:
* [ResourcePrincipalAuthenticationDetailsProvider](https://docs.cloud.oracle.com/en-us/iaas/tools/java/latest/com/oracle/bmc/auth/ResourcePrincipalAuthenticationDetailsProvider.html) to authenticate
* [ObjectStorageClient](https://docs.oracle.com/en-us/iaas/tools/java/latest/com/oracle/bmc/objectstorage/ObjectStorageClient.html) to interact with Object Storage

As you make your way through this tutorial, look out for this icon ![user input icon](./images/userinput.png).
Whenever you see it, it's time for you to perform an action.


## Prerequisites

Before you deploy this sample function, make sure you have run steps A, B 
and C of the [Oracle Functions Quick Start Guide for Cloud Shell](https://www.oracle.com/webfolder/technetwork/tutorials/infographics/oci_functions_cloudshell_quickview/functions_quickview_top/functions_quickview/index.html)
* A - Set up your tenancy
* B - Create application
* C - Set up your Cloud Shell dev environment


## List Applications 

Assuming you have successfully completed the prerequisites, you should see your 
application in the list of applications.

```
fn ls apps
```


## Create or Update your Dynamic Group

In order to use other OCI Services, your function must be part of a dynamic 
group. For information on how to create a dynamic group, refer to the 
[documentation](https://docs.cloud.oracle.com/iaas/Content/Identity/Tasks/managingdynamicgroups.htm#To).

When specifying the *Matching Rules*, we suggest matching all functions in a compartment with:

```
ALL {resource.type = 'fnfunc', resource.compartment.id = 'ocid1.compartment.oc1..aaaaaxxxxx'}
```


## Create or Update IAM Policies

Create policy statements that allow the dynamic group to `read objectstorage-namespaces` and 
`inspect buckets` in the functions related compartment.

![user input icon](./images/userinput.png)

Your policy should look something like this:
```
Allow dynamic-group <dynamic-group-name> to read objectstorage-namespaces in compartment <compartment-name>
Allow dynamic-group <dynamic-group-name> to inspect buckets in compartment <compartment-name>
```
e.g.
```
Allow dynamic-group demo-func-dyn-group to read objectstorage-namespaces in compartment demo-func-compartment
Allow dynamic-group demo-func-dyn-group to inspect buckets in compartment demo-func-compartment
```

For more information on how to create policies, go [here](https://docs.cloud.oracle.com/iaas/Content/Identity/Concepts/policysyntax.htm).


## Review and customize your function

Review the following files in the current folder:
- [pom.xml](./pom.xml) specifies all the dependencies for your function
- [func.yaml](./func.yaml) contains metadata about your function and declares properties
- [Dockerfile](./Dockerfile) contains multistage native-image build and packaging commands
- [src/main/java/com/example/fn/ObjectStorageBucketsList.java](./src/main/java/com/example/fn/ObjectStorageBucketsList.java) which contains the Java code

The name of your function *fn-gvm-oci-list-buckets-netty* is specified in [func.yaml](./func.yaml).


## Deploy the function

In Cloud Shell, run the *fn deploy* command to build the function and its dependencies as a Docker image, 
push the image to the specified Docker registry, and deploy the function to Oracle Functions 
in the application created earlier:

![user input icon](./images/userinput.png)
```
fn -v deploy --app <app-name>
```
e.g.
```
fn -v deploy --app myapp
```


## Test

Use the *fn* CLI to invoke your function with your app name and the compartment OCID:

![user input icon](./images/userinput.png)
```
echo -n '<compartment-ocid>' | fn invoke <app-name> <function-name>
```
e.g.
```
echo -n 'ocid1.compartment.oc1...2jn3htfoobar' | fn invoke myapp fn-gvm-oci-list-buckets-netty
```
You should see a list of bucket names from your compartment appear on your terminal.


## Monitoring Functions

Learn how to configure basic observability for your function using metrics, alarms and email alerts:
* [Basic Guidance for Monitoring your Functions](../basic-observability/functions.md)


## Troubleshooting

1) "Class 'com.example.fn.ObjectStorageBucketsList' not found in function jar. It's likely that the 'cmd' entry in func.yaml is incorrect."

Solution: Missing reflection metadata.
