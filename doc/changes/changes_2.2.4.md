# Virtual Schema for BigQuery 2.2.4, released 2023-10-25

Code name: Dependency Upgrade on Top of 2.2.3

## Summary

This release fixes vulnerability CVE-2023-5072 in transitive test dependency to `org.json:json` via `com.google.cloud:google-cloud-bigquery` by updating dependencies.

## Security

* #27: Fixed vulnerability CVE-2023-5072 in `org.json:json`

## Dependency Updates

### Test Dependency Updates

* Updated `com.google.cloud:google-cloud-bigquery:2.33.1` to `2.33.2`
* Added `org.json:json:20231013`
* Updated `org.mockito:mockito-junit-jupiter:5.5.0` to `5.6.0`
* Updated `org.testcontainers:jdbc:1.19.0` to `1.19.1`
* Updated `org.testcontainers:junit-jupiter:1.19.0` to `1.19.1`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.3.0` to `1.3.1`
* Updated `com.exasol:project-keeper-maven-plugin:2.9.12` to `2.9.14`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.4.0` to `3.4.1`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.16.0` to `2.16.1`
* Updated `org.jacoco:jacoco-maven-plugin:0.8.10` to `0.8.11`
* Updated `org.sonarsource.scanner.maven:sonar-maven-plugin:3.9.1.2184` to `3.10.0.2594`
