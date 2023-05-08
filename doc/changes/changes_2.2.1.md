# Virtual Schema for BigQuery 2.2.1, released 2023-05-08

Code name: Dependency Upgrade on Top of 2.2.0

## Summary

This release updates dependencies and stops ignoring vulnerabilities sonatype-2020-0026, sonatype-2020-0926, and sonatype-2022-6438 that had been masked in the past.

This release ignores vulnerability CVE-2020-8908 in transitive dependency `com.google.guava:guava:jar:31.1` via `com.google.cloud:google-cloud-bigquery` as guava is only used in tests while production code is not affected.

## Features

* #20: Fixed dependency check vulnerability findings

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.0.0` to `2.0.1`
* Updated `com.exasol:hamcrest-resultset-matcher:1.5.2` to `1.6.0`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.14.2` to `2.15.0`
* Updated `com.google.cloud:google-cloud-bigquery:2.23.2` to `2.25.0`
* Updated `org.junit.jupiter:junit-jupiter:5.9.2` to `5.9.3`
* Updated `org.mockito:mockito-junit-jupiter:5.2.0` to `5.3.1`
* Updated `org.testcontainers:jdbc:1.17.6` to `1.18.0`
* Updated `org.testcontainers:junit-jupiter:1.17.6` to `1.18.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.2.2` to `1.2.3`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.4` to `2.9.7`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.10.1` to `3.11.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.2.1` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M8` to `3.0.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M8` to `3.0.0`
* Added `org.basepom.maven:duplicate-finder-maven-plugin:1.5.1`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.3.0` to `1.4.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.14.2` to `2.15.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.8` to `0.8.9`
