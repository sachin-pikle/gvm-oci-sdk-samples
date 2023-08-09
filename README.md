# gvm-oci-sdk-samples

## Build a fat jar

```
./mvnw clean compile assembly:single
```

## Run fat jar

### Valid Compartment OCID

```
java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba
```

### Invalid Compartment OCID

```
java -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar abcd
```

## Capture reachability metadata

```
java -agentlib:native-image-agent=config-output-dir=META-INF/native-image -jar target/my-app-1.0-SNAPSHOT-jar-with-dependencies.jar ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba
```

Move/merge the metadata in `src/main/resources/META-INF/native-image` before building a native executable.


## Build a native executable

```
./mvnw -Pnative -DskipTests package
```


## Run native executable

### Valid Compartment OCID

```
./target/my-app ocid1.compartment.oc1..aaaaaaaauivfa3pu7pcn6yslq2ibww566heqmbeo36ah3vzhm6muyospeqba
```

### Invalid Compartment OCID

```
./target/my-app abcd
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


3. Native image build fails with the following error with Oracle GraalVM for JDK 17 and above:

```
Error: Class-path entry file:///Users/spikle/.m2/repository/org/graalvm/sdk/graal-sdk/21.3.1/graal-sdk-21.3.1.jar contains class org.graalvm.nativeimage.impl.CTypeConversionSupport. This class is part of the image builder itself (in jrt:/org.graalvm.sdk) and must not be passed via -cp. This can be caused by a fat-jar that illegally includes svm.jar (or graal-sdk.jar) due to its build-time dependency on it. As a workaround, -H:+AllowDeprecatedBuilderClassesOnImageClasspath allows turning this error into a warning. Note that this option is deprecated and will be removed in a future version.
com.oracle.svm.core.util.UserError$UserException: Class-path entry file:///Users/spikle/.m2/repository/org/graalvm/sdk/graal-sdk/21.3.1/graal-sdk-21.3.1.jar contains class org.graalvm.nativeimage.impl.CTypeConversionSupport. This class is part of the image builder itself (in jrt:/org.graalvm.sdk) and must not be passed via -cp. This can be caused by a fat-jar that illegally includes svm.jar (or graal-sdk.jar) due to its build-time dependency on it. As a workaround, -H:+AllowDeprecatedBuilderClassesOnImageClasspath allows turning this error into a warning. Note that this option is deprecated and will be removed in a future version.
        at com.oracle.svm.core.util.UserError.abort(UserError.java:73)
        at com.oracle.svm.hosted.NativeImageClassLoaderSupport.reportBuilderClassesInApplication(NativeImageClassLoaderSupport.java:819)
        at com.oracle.svm.hosted.ImageClassLoader.loadAllClasses(ImageClassLoader.java:105)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.buildImage(NativeImageGeneratorRunner.java:296)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.build(NativeImageGeneratorRunner.java:612)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.start(NativeImageGeneratorRunner.java:134)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.main(NativeImageGeneratorRunner.java:94)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner$JDK9Plus.main(NativeImageGeneratorRunner.java:626)
com.oracle.svm.driver.NativeImage$NativeImageError
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.showError(NativeImage.java:1982)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.build(NativeImage.java:1598)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.performBuild(NativeImage.java:1557)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.main(NativeImage.java:1531)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

Solution: Add `<arg>-H:+AllowDeprecatedBuilderClassesOnImageClasspath</arg>` to the pom.xml before running the native image build with Oracle GraalVM for JDK 17 and above.


4. Native image build fails with the following error with GraalVM Enterprise Edition 22.3.x and lower:

```
Error: Could not find option 'AllowDeprecatedBuilderClassesOnImageClasspath'. Use -H:PrintFlags= to list all available options.
Error: Image build request failed with exit status 1
com.oracle.svm.driver.NativeImage$NativeImageError: Image build request failed with exit status 1
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.showError(NativeImage.java:1730)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.build(NativeImage.java:1427)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.performBuild(NativeImage.java:1387)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.main(NativeImage.java:1374)
```

Solution: Comment `<arg>-H:+AllowDeprecatedBuilderClassesOnImageClasspath</arg>` in the pom.xml before running the native image build with GraalVM Enterprise Edition 22.3.x and lower.



5. Native image build fails with `DARWIN does not support building static executable images` on MacOS.

```
[1/8] Initializing...                                                                                    (7.1s @ 0.22GB)
 Java version: 17.0.7+8-LTS, vendor version: Oracle GraalVM 17.0.7+8.1
 Graal compiler: optimization level: b, target machine: x86-64-v3, PGO: off
 C compiler: cc (apple, x86_64, 14.0.3)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
...
[8/8] Creating image...       [***]                                                                      (0.0s @ 1.76GB)
Error: DARWIN does not support building static executable images.
com.oracle.svm.core.util.UserError$UserException: DARWIN does not support building static executable images.
        at com.oracle.svm.core.util.UserError.abort(UserError.java:73)
        at com.oracle.svm.hosted.image.CCLinkerInvocation$DarwinCCLinkerInvocation.setOutputKind(CCLinkerInvocation.java:421)
        at com.oracle.svm.hosted.image.CCLinkerInvocation.getCommand(CCLinkerInvocation.java:203)
        at com.oracle.svm.hosted.image.NativeImageViaCC.write(NativeImageViaCC.java:116)
        at com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:724)
        at com.oracle.svm.hosted.NativeImageGenerator.run(NativeImageGenerator.java:539)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.buildImage(NativeImageGeneratorRunner.java:408)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.build(NativeImageGeneratorRunner.java:612)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.start(NativeImageGeneratorRunner.java:134)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner.main(NativeImageGeneratorRunner.java:94)
        at com.oracle.svm.hosted.NativeImageGeneratorRunner$JDK9Plus.main(NativeImageGeneratorRunner.java:626)
------------------------------------------------------------------------------------------------------------------------
                        9.7s (7.0% of total time) in 94 GCs | Peak RSS: 5.46GB | CPU load: 5.44
========================================================================================================================
Finished generating 'my-app' in 2m 19s.
com.oracle.svm.driver.NativeImage$NativeImageError
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.showError(NativeImage.java:1982)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.build(NativeImage.java:1598)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.performBuild(NativeImage.java:1557)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.main(NativeImage.java:1531)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

Solution: Comment `<arg>-H:+StaticExecutableWithDynamicLibC</arg>` in the pom.xml before running the native image build on MacOS.

6. Native image build fails with the following error:

```
[1/8] Initializing...                                                                                    (0.0s @ 0.16GB)
------------------------------------------------------------------------------------------------------------------------
                        0.3s (5.9% of total time) in 11 GCs | Peak RSS: 0.61GB | CPU load: 4.47
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /Users/spikle/Documents/code/graal/gvm-oci-sdk-samples/target/svm_err_b_20230808T111937.203_pid10673.md (build_info)
========================================================================================================================
Failed generating 'my-app' after 3.3s.

The build process encountered an unexpected error:

java.lang.IllegalAccessError: class com.oracle.bmc.graalvm.BouncyCastleFeature (in unnamed module @0x4e5da590) cannot access class org.graalvm.nativeimage.impl.RuntimeClassInitializationSupport (in module org.graalvm.sdk) because module org.graalvm.sdk does not export org.graalvm.nativeimage.impl to unnamed module @0x4e5da590
        at com.oracle.bmc.graalvm.BouncyCastleFeature.afterRegistration(BouncyCastleFeature.java:23)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGenerator.lambda$setupNativeImage$14(NativeImageGenerator.java:879)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.FeatureHandler.forEachFeature(FeatureHandler.java:86)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGenerator.setupNativeImage(NativeImageGenerator.java:879)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:579)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGenerator.run(NativeImageGenerator.java:539)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGeneratorRunner.buildImage(NativeImageGeneratorRunner.java:408)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGeneratorRunner.build(NativeImageGeneratorRunner.java:612)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGeneratorRunner.start(NativeImageGeneratorRunner.java:134)
        at org.graalvm.nativeimage.builder/com.oracle.svm.hosted.NativeImageGeneratorRunner.main(NativeImageGeneratorRunner.java:94)

com.oracle.svm.driver.NativeImage$NativeImageError
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.showError(NativeImage.java:1982)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.build(NativeImage.java:1598)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.performBuild(NativeImage.java:1557)
        at org.graalvm.nativeimage.driver/com.oracle.svm.driver.NativeImage.main(NativeImage.java:1531)
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

Solution: Add the following to your pom.xml:

```
              <environment>
                <USE_NATIVE_IMAGE_JAVA_PLATFORM_MODULE_SYSTEM>false</USE_NATIVE_IMAGE_JAVA_PLATFORM_MODULE_SYSTEM>
              </environment>
```


7. In case of invalid compartment OCID, native executable fails to run with the following error:

```
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Cannot construct instance of `com.oracle.bmc.http.internal.ResponseHelper$ErrorCodeAndMessage` (no Creators, like default constructor, exist): cannot deserialize from Object value (no delegate- or property-based Creator)
 at [Source: (org.glassfish.jersey.message.internal.ReaderInterceptorExecutor$UnCloseableInputStream); line: 2, column: 3]
```

Solution: Add the following element to your reflect-config.json:

```
  {
    "name":"com.oracle.bmc.http.internal.ResponseHelper$ErrorCodeAndMessage",
    "methods":[{"name":"<init>","parameterTypes":["java.lang.String","java.lang.String","java.lang.String","java.lang.String","java.util.Map"] }]
  }
```
