# Virtual Schema for BigQuery 4.0.2, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-110879 in com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test

## Summary

This release fixes the following vulnerability:

### CVE-2026-110879 (CWE-502) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
jackson-databind - Deserialization of Untrusted Data
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-110879?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-110879
* https://github.com/FasterXML/jackson/wiki/Jackson-Release-2.18.12
* https://github.com/FasterXML/jackson-databind/pull/6287

## Security

* #92: Fixed vulnerability CVE-2026-110879 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.12` to `3.0.1`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.4`
* Updated `com.exasol:test-db-builder-java:4.0.1` to `4.0.3`
* Updated `com.exasol:udf-debugging-java:0.6.18` to `0.6.22`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.1.0`
* Updated `com.fasterxml.jackson.core:jackson-core:2.22.1` to `2.22.4`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.1` to `2.22.4`
* Updated `com.google.cloud:google-cloud-bigquery:2.68.0` to `2.73.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.4` to `6.1.3`
* Updated `org.mockito:mockito-junit-jupiter:5.23.0` to `5.24.0`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.8.1`
