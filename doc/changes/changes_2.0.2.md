# Virtual Schema for BigQuery 2.0.2, released 2022-??-??

Code name: Fix data type conversion

## Summary

This release fixes conversions of the following BigQuery data types: DATE, TIMESTAMP, DATETIME and GEOGRAPHY.

It also fixes the following vulnerabilities by updating dependencies:

* CVE-2022-24823
* [sonatype-2021-0818](https://ossindex.sonatype.org/vulnerability/sonatype-2021-0818)

## Bugfixes

* #7: Fixed conversion of BigQuery data types DATE, ....

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:9.0.4` to `9.0.5`

### Test Dependency Updates

* Added `com.exasol:exasol-test-setup-abstraction-java:0.3.2`
* Added `com.exasol:hamcrest-resultset-matcher:1.5.1`
* Added `com.exasol:test-db-builder-java:3.3.3`
* Added `com.exasol:udf-debugging-java:0.6.4`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.4` to `9.0.5`
* Added `com.google.cloud:google-cloud-bigquery:2.14.0`
* Added `io.grpc:grpc-core:1.48.0`
* Added `io.netty:netty-common:4.1.79.Final`
* Updated `org.junit.jupiter:junit-jupiter:5.8.1` to `5.8.2`
* Updated `org.mockito:mockito-junit-jupiter:4.1.0` to `4.6.1`
* Added `org.testcontainers:jdbc:1.17.3`
* Added `org.testcontainers:junit-jupiter:1.17.3`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.3.1` to `0.4.0`
* Updated `com.exasol:error-code-crawler-maven-plugin:0.6.0` to `1.1.1`
* Updated `com.exasol:project-keeper-maven-plugin:1.3.2` to `2.5.0`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.13` to `0.15`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.1.0` to `2.5`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.8.1` to `3.10.1`
* Updated `org.apache.maven.plugins:maven-deploy-plugin:3.0.0-M1` to `2.7`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M3` to `3.0.0`
* Added `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.0.0-M1` to `2.4`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.0` to `3.2.2`
* Updated `org.apache.maven.plugins:maven-resources-plugin:3.2.0` to `2.6`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.3`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M3` to `3.0.0-M5`
* Added `org.codehaus.mojo:flatten-maven-plugin:1.2.7`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.7` to `2.10.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.5` to `0.8.8`
* Added `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184`
* Updated `org.sonatype.ossindex.maven:ossindex-maven-plugin:3.1.0` to `3.2.0`
