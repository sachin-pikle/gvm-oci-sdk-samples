# gvm-oci-sdk-samples

## Build a fat jar

```
./mvnw clean compile assembly:single
```

## Run fat jar

```
java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba
```

## Build a native executable

```
./mvnw -Pnative -DskipTests package
```


## Run native executable

```
./target/my-app ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba
```

## Troubleshooting

1. The Jar build fails with the following error:

```
[ERROR] Unknown lifecycle phase "$USER_HOME_DIR/.m2".
```

Solution:

```
$ env | grep HOME
...
MAVEN_CONFIG=$USER_HOME_DIR/.m2
...

$ unset MAVEN_CONFIG
```

2. Native image build fails with `java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl`.

```
java.lang.ExceptionInInitializerError
        at org.apache.http.conn.ssl.SSLConnectionSocketFactory.<clinit>(SSLConnectionSocketFactory.java:151)
        at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.getDefaultRegistry(PoolingHttpClientConnectionManager.java:116)
        at org.apache.http.impl.conn.PoolingHttpClientConnectionManager.<init>(PoolingHttpClientConnectionManager.java:123)
        at com.oracle.bmc.http.client.jersey.JerseyHttpClientBuilder.buildDefaultPoolingHttpClientConnectionManagerForApacheConnector(JerseyHttpClientBuilder.java:392)
        at com.oracle.bmc.http.client.jersey.JerseyHttpClientBuilder.build(JerseyHttpClientBuilder.java:249)
        at com.oracle.bmc.http.internal.BaseClient.setEndpoint(BaseClient.java:346)
        at com.oracle.bmc.http.internal.BaseClient.setRegion(BaseClient.java:374)
        at com.oracle.bmc.core.ComputeClient.setRegion(ComputeClient.java:113)
        at com.oracle.bmc.http.internal.BaseClient.<init>(BaseClient.java:132)
        at com.oracle.bmc.http.internal.BaseSyncClient.<init>(BaseSyncClient.java:36)
        at com.oracle.bmc.core.ComputeClient.<init>(ComputeClient.java:37)
        at com.oracle.bmc.core.ComputeClient$Builder.build(ComputeClient.java:107)
        at com.gvm.samples.Instances.listInstances(Instances.java:50)
        at com.gvm.samples.App.main(App.java:29)
Caused by: org.apache.commons.logging.LogConfigurationException: java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl (Caused by java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl)
        at org.apache.commons.logging.LogFactory.createFactory(LogFactory.java:1158)
        at org.apache.commons.logging.LogFactory$2.run(LogFactory.java:960)
        at java.base@17.0.7/java.security.AccessController.executePrivileged(AccessController.java:168)
        at java.base@17.0.7/java.security.AccessController.doPrivileged(AccessController.java:318)
        at org.apache.commons.logging.LogFactory.newFactory(LogFactory.java:957)
        at org.apache.commons.logging.LogFactory.getFactory(LogFactory.java:624)
        at org.apache.commons.logging.LogFactory.getLog(LogFactory.java:655)
        at org.apache.http.conn.ssl.AbstractVerifier.<init>(AbstractVerifier.java:61)
        at org.apache.http.conn.ssl.AllowAllHostnameVerifier.<init>(AllowAllHostnameVerifier.java:44)
        at org.apache.http.conn.ssl.AllowAllHostnameVerifier.<clinit>(AllowAllHostnameVerifier.java:46)
        ... 14 more
Caused by: java.lang.ClassNotFoundException: org.apache.commons.logging.impl.LogFactoryImpl
        at java.base@17.0.7/jdk.internal.loader.BuiltinClassLoader.loadClass(BuiltinClassLoader.java:52)
        at java.base@17.0.7/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(ClassLoaders.java:188)
        at java.base@17.0.7/java.lang.ClassLoader.loadClass(ClassLoader.java:132)
        at org.apache.commons.logging.LogFactory.createFactory(LogFactory.java:1020)
        ... 23 more
```

Solution: Missing reachability metadata. Use tracing agent to generate it.
 