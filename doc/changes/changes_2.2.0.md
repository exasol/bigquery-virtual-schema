# Virtual Schema for BigQuery 2.2.0, released 2023-03-16

Code name: Dependency Upgrade on top of 2.1.1

## Summary

Updated dependencies to fix vulnerability CVE-2022-45688 in test dependency [org.json:json:jar:20220924](https://ossindex.sonatype.org/component/pkg:maven/org.json/json@20220924?utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1).

Please note that updated dependency `virtual-schema-common-jdbc` adds support for a new adapter property [`MAX_TABLE_COUNT`](https://github.com/exasol/virtual-schema-common-jdbc#property-max_table_count) and fixes ambiguous results by escaping SQL wildcards such as underscore `_` and percent `%` in names of catalogs, schemas, and tables when retrieving column metadata from JDBC driver.

## Security

* #18: Fixed vulnerabilities

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:1.0.0` to `1.0.1`
* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.5.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:0.3.2` to `2.0.0`
* Updated `com.exasol:test-db-builder-java:3.4.1` to `3.4.2`
* Updated `com.exasol:udf-debugging-java:0.6.4` to `0.6.8`
* Updated `com.exasol:virtual-schema-common-jdbc:10.0.1` to `10.5.0`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.13.4.2` to `2.14.2`
* Updated `com.google.cloud:google-cloud-bigquery:2.17.1` to `2.23.2`
* Updated `org.junit.jupiter:junit-jupiter:5.9.1` to `5.9.2`
* Updated `org.mockito:mockito-junit-jupiter:4.8.1` to `5.2.0`
* Updated `org.testcontainers:jdbc:1.17.5` to `1.17.6`
* Updated `org.testcontainers:junit-jupiter:1.17.5` to `1.17.6`

### Plugin Dependency Updates

* Updated `com.exasol:artifact-reference-checker-maven-plugin:0.4.0` to `0.4.2`
* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.2` to `1.2.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.8.0` to `2.9.4`
* Updated `io.github.zlika:reproducible-build-maven-plugin:0.15` to `0.16`
* Updated `org.apache.maven.plugins:maven-assembly-plugin:3.3.0` to `3.5.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.1.0` to `3.2.1`
* Updated `org.apache.maven.plugins:maven-failsafe-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.apache.maven.plugins:maven-jar-plugin:3.2.2` to `3.3.0`
* Updated `org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5` to `3.0.0-M8`
* Updated `org.codehaus.mojo:flatten-maven-plugin:1.2.7` to `1.3.0`
* Updated `org.codehaus.mojo:versions-maven-plugin:2.10.0` to `2.14.2`
