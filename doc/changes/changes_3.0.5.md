# Virtual Schema for BigQuery 3.0.5, released 2024-02-??

Code name: Fix CVE-2025-25193 and CVE-2025-24970 in test dependencies

## Summary

This release fixes CVE-2025-25193 and CVE-2025-24970 in test dependencies.

## Security

* #45: Fixed CVE-2025-25193 in `io.netty:netty-common:jar:4.1.115.Final:test`
* #44: Fixed CVE-2025-24970 in `io.netty:netty-handler:jar:4.1.112.Final:test`

## Dependency Updates

### Runtime Dependency Updates

* Added `org.slf4j:slf4j-jdk14:1.7.36`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.5` to `2.1.7`
* Updated `com.exasol:udf-debugging-java:0.6.13` to `0.6.15`
* Updated `com.google.cloud:google-cloud-bigquery:2.43.3` to `2.48.0`
* Removed `io.netty:netty-common:4.1.115.Final`
* Updated `org.junit.jupiter:junit-jupiter:5.11.3` to `5.11.4`
* Updated `org.mockito:mockito-junit-jupiter:5.14.2` to `5.15.2`
* Removed `org.slf4j:slf4j-jdk14:2.0.16`
* Updated `org.testcontainers:jdbc:1.20.3` to `1.20.4`
* Updated `org.testcontainers:junit-jupiter:1.20.3` to `1.20.4`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.4.0` to `4.5.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.1` to `3.5.2`
* Updated `org.apache.maven.plugins:maven-site-plugin:3.9.1` to `3.21.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.1` to `3.5.2`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.17.1` to `2.18.0`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121` to `5.0.0.4389`
