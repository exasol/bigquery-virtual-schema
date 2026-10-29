# Virtual Schema for BigQuery 5.0.0, released 2026-10-30

Code name: Security fixes

## Summary

This update fixes 19 dependency vulnerabilities and records one finding awaiting a matching repository issue.

## Breaking Change

This project now requires Java 17 for building and running and does not support Exasol v8 anymore.

## Security

* #74: Fixed CVE-2026-68498 (unbounded resource allocation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #79: Fixed CVE-2026-89407 (quadratic-time number validation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #80: Fixed CVE-2026-89425 (unbounded error-token accumulation) in dependency `com.fasterxml.jackson.core:jackson-core:jar:2.22.1:test`
* #90: Fixed CVE-2026-102554 (unsafe deserialization) in dependency `com.google.guava:guava:jar:33.5.0-jre:test`
* #88: Fixed CVE-2026-110262 (unbounded resource allocation) in dependency `org.ow2.asm:asm-commons:jar:9.9:test`
* #89: Fixed CVE-2026-110270 (algorithmic complexity) in dependency `org.ow2.asm:asm-commons:jar:9.9:test`
* #82: Fixed CVE-2026-110257 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #83: Fixed CVE-2026-110258 (uncontrolled memory allocation) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #84: Fixed CVE-2026-110259 (uncontrolled recursion) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #85: Fixed CVE-2026-110260 (algorithmic complexity) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #86: Fixed CVE-2026-110261 (algorithmic complexity) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #87: Fixed CVE-2026-110263 (uncontrolled recursion) in dependency `org.ow2.asm:asm:jar:9.9:test`
* #92: Fixed CVE-2026-110879 (unsafe deserialization) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #69: Fixed CVE-2026-19032 (unsafe URI scheme selection) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #70: Fixed CVE-2026-68497 (unbounded resource allocation) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #75: Fixed CVE-2026-91776 (unbounded type ID cache growth) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #76: Fixed CVE-2026-91777 (denial of service) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #77: Fixed CVE-2026-83557 (unsafe deserialization) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #72: Fixed CVE-2026-86231 (certificate revocation validation) in dependency `com.github.mwiede:jsch:jar:2.28.4:test`
* CVE-2026-116486 (incorrect symbolic name mapping) in dependency `org.objenesis:objenesis:jar:3.3:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.1`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.12` to `3.0.1`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.1` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.18` to `0.6.22`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.1`
* Updated `com.fasterxml.jackson.core:jackson-core:2.22.1` to `2.22.4`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.1` to `2.22.4`
* Updated `com.google.cloud:google-cloud-bigquery:2.68.0` to `2.74.0`
* Removed `org.junit.jupiter:junit-jupiter-params:5.14.4`
* Added `org.junit.jupiter:junit-jupiter:6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.1.0` to `2.1.3`
* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.2`
* Updated `io.github.git-commit-id:git-commit-id-maven-plugin:10.0.0` to `10.0.1`
* Updated `org.apache.maven.plugins:maven-artifact-plugin:3.6.1` to `3.7.0`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.15.0` to `3.16.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.4` to `3.2.0`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.5.0` to `3.5.1`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.6` to `3.6.1`
* Updated `org.apache.maven.plugins:maven-toolchains-plugin:3.2.0` to `3.3.0`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.7.3` to `1.8.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.21.0` to `2.23.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.15` to `0.8.16`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970` to `5.8.0.7211`
