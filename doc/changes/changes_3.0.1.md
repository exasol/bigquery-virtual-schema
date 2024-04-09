# Virtual Schema for BigQuery 3.0.1, released 2024-04-09

Code name: Fix CVE-2024-29025 in dependencies

## Summary

This release fixed vulnerability CVE-2024-29025 in dependencies.

## Security

* #33: Fixed CVE-2024-29025 in `io.netty:netty-codec-http:jar:4.1.107.Final:test`

**Excluded Vulnerability** We accept vulnerability CVE-2017-10355 (CWE-833: Deadlock) in test dependency `xerces:xercesImpl:jar:2.12.2` as we assume that we only connect to the known endpoint ExaOperations.

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.0` to `2.1.2`
* Updated `com.exasol:test-db-builder-java:3.5.3` to `3.5.4`
* Updated `com.exasol:udf-debugging-java:0.6.12` to `0.6.13`
* Updated `com.google.cloud:google-cloud-bigquery:2.38.1` to `2.38.2`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.1` to `2.0.2`
* Updated `com.exasol:project-keeper-maven-plugin:4.2.0` to `4.3.0`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.6.0` to `3.7.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.12.1` to `3.13.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.11` to `0.8.12`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594` to `3.11.0.3922`
