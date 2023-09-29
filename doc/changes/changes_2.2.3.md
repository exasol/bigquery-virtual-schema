# Virtual Schema for BigQuery 2.2.3, released 2023-09-29

Code name: Fix CVE-2023-42503 in test dependency

## Summary

This release fixes CVE-2023-42503 in test dependency `org.apache.commons:commons-compress`.

**Known issue:** Transitive test dependency `io.netty:netty-handler` of `software.amazon.awssdk:cloudformation` contains vulnerability CVE-2023-4586 (CWE-300: Channel Accessible by Non-Endpoint ('Man-in-the-Middle') (6.5)). We assume that the AWS client's usage of netty-handler is not affected by the vulnerability.

## Security

* #42: Fixed CVE-2023-42503 in test dependency `org.apache.commons:commons-compress`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:10.5.0` to `11.0.2`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.2` to `2.0.4`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.0` to `1.6.1`
* Updated `com.exasol:test-db-builder-java:3.4.2` to `3.5.1`
* Updated `com.exasol:udf-debugging-java:0.6.8` to `0.6.11`
* Updated `com.exasol:virtual-schema-common-jdbc:10.5.0` to `11.0.2`
* Removed `com.fasterxml.jackson.core:jackson-databind:2.15.2`
* Updated `com.google.cloud:google-cloud-bigquery:2.29.0` to `2.33.1`
* Updated `org.junit.jupiter:junit-jupiter:5.9.3` to `5.10.0`
* Updated `org.mockito:mockito-junit-jupiter:5.4.0` to `5.5.0`
* Added `org.slf4j:slf4j-jdk14:1.7.36`
* Updated `org.testcontainers:jdbc:1.18.3` to `1.19.0`
* Updated `org.testcontainers:junit-jupiter:1.18.3` to `1.19.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.3` to `1.3.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.7` to `2.9.12`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.5.0` to `3.6.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.3.0` to `3.4.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0` to `3.1.2`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0` to `3.1.2`
* Updated `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1` to `2.0.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.4.1` to `1.5.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.15.0` to `2.16.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.9` to `0.8.10`
