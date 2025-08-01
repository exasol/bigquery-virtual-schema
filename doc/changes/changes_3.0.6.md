# Virtual Schema for BigQuery 3.0.6, released 2025-08-01

Code name: Fixed vulnerabilities in test dependencies

## Summary

This release updates dependencies to fix CVE-2025-48924 in transitive test 
`org.apache.commons:commons-lang3:jar:3.17.0:test`.

We also added an exception for the OSSIndex for CVE-2024-55551, which is a false positive in Exasol's JDBC driver.
This issue has been fixed quite a while back now, but the OSSIndex unfortunately does not contain the fix version of 24.2.1 (2024-12-10) set.

## Security

* #53: Fix CVE-2025-48924 in `org.apache.commons:commons-lang3:jar:3.16.0:test`
* #51: Fix CVE-2024-55551 in `com.exasol:exasol-jdbc:jar:24.2.1:test`

## Dependency Updates

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.7` to `2.1.8`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.0` to `1.7.1`
* Updated `com.exasol:test-db-builder-java:3.6.0` to `3.6.2`
* Updated `com.exasol:udf-debugging-java:0.6.15` to `0.6.17`
* Updated `com.google.cloud:google-cloud-bigquery:2.48.0` to `2.54.0`
* Added `org.junit.jupiter:junit-jupiter-params:5.13.4`
* Removed `org.junit.jupiter:junit-jupiter:5.11.4`
* Updated `org.mockito:mockito-junit-jupiter:5.15.2` to `5.18.0`
* Updated `org.testcontainers:jdbc:1.20.4` to `1.21.3`
* Updated `org.testcontainers:junit-jupiter:1.20.4` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.2` to `0.4.3`
* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.3` to `2.0.4`
* Updated `com.exasol:project-keeper-maven-plugin:4.5.0` to `5.2.3`
* Added `io.github.git-commit-id:git-commit-id-maven-plugin:9.0.1`
* Removed `io.github.zlika:reproducible-build-maven-plugin:0.17`
* Added `org.apache.maven.plugins:maven-artifact-plugin:3.6.0`
* Updated `org.apache.maven.plugins:maven-clean-plugin:3.4.0` to `3.4.1`
* Updated `org.apache.maven.plugins:maven-compiler-plugin:3.13.0` to `3.14.0`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.5.2` to `3.5.3`
* Updated `org.apache.maven.plugins:maven-install-plugin:3.1.3` to `3.1.4`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.5.2` to `3.5.3`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.6.0` to `1.7.0`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.12` to `0.8.13`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389` to `5.1.0.4751`
