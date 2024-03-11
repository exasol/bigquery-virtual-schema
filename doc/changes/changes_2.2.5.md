# Virtual Schema for BigQuery 2.2.5, released 2024-03-11

Code name: Fixed vulnerabilities CVE-2024-25710 and CVE-2024-26308 in test dependencies

This is a security release in which we updated test dependency `com.exasol:exasol-test-setup-abstraction-java` to fix vulnerabilities CVE-2024-25710 and CVE-2024-26308 in its transitive dependencies.

## Summary

## Security

* #60: Fixed vulnerability CVE-2024-25710

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:11.0.2` to `12.0.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.4` to `2.1.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.6.1` to `1.6.5`
* Updated `com.exasol:test-db-builder-java:3.5.1` to `3.5.3`
* Updated `com.exasol:udf-debugging-java:0.6.11` to `0.6.12`
* Updated `com.exasol:virtual-schema-common-jdbc:11.0.2` to `12.0.0`
* Updated `com.google.cloud:google-cloud-bigquery:2.33.2` to `2.38.1`
* Updated `org.json:json:20231013` to `20240303`
* Updated `org.junit.jupiter:junit-jupiter:5.10.0` to `5.10.2`
* Updated `org.mockito:mockito-junit-jupiter:5.6.0` to `5.11.0`
* Updated `org.slf4j:slf4j-jdk14:1.7.36` to `2.0.12`
* Updated `org.testcontainers:jdbc:1.19.1` to `1.19.7`
* Updated `org.testcontainers:junit-jupiter:1.19.1` to `1.19.7`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.1` to `2.0.0`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.14` to `4.1.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.11.0` to `3.12.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.1.2` to `3.2.5`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.1.2` to `3.2.5`
* Added `org.apache.maven.plugins:maven-toolchains-plugin:3.1.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.5.0` to `1.6.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.1` to `2.16.2`
