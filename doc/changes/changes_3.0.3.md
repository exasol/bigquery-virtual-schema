# Virtual Schema for BigQuery 3.0.3, released 2024-09-23

Code name: Fix CVE-2024-7254 in test dependency `com.google.protobuf:protobuf-java:3.25.2`

## Summary

This release fixes CVE-2024-7254 in transitive test dependency `com.google.protobuf:protobuf-java:3.25.2`.

## Security

* #39: Fixed CVE-2024-7254 test dependency `com.google.protobuf:protobuf-java:3.25.2`

## Dependency Updates

### Test Dependency Updates

* Added `com.exasol:bucketfs-java:3.2.0`
* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.2` to `2.1.4`
* Added `com.exasol:exasol-testcontainers:7.1.1`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.5` to `1.7.0`
* Updated `com.google.cloud:google-cloud-bigquery:2.38.2` to `2.42.3`
* Added `com.google.protobuf:protobuf-java:3.25.5`
* Updated `org.hamcrest:hamcrest:2.2` to `3.0`
* Updated `org.junit.jupiter:junit-jupiter:5.10.2` to `5.11.0`
* Updated `org.mockito:mockito-junit-jupiter:5.11.0` to `5.13.0`
* Updated `org.slf4j:slf4j-jdk14:2.0.12` to `2.0.16`
* Updated `org.testcontainers:jdbc:1.19.7` to `1.20.1`
* Updated `org.testcontainers:junit-jupiter:1.19.7` to `1.20.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.2` to `2.0.3`
* Updated `com.exasol:project-keeper-maven-plugin:4.3.0` to `4.3.3`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.1` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.3.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0` to `3.2.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.11.0.3922` to `4.0.0.4121`
