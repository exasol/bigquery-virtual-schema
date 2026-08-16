# Virtual Schema for BigQuery 4.0.2, released 2026-??-??

Code name: Fixed vulnerabilities CVE-2026-19032, CVE-2026-68497

## Summary

This release fixes the following 2 vulnerabilities:

### CVE-2026-19032 (CWE-470) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
com.fasterxml.jackson.core/jackson-databind - Unrestricted URI schemes in Path deserialization
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-19032?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-19032
* https://github.com/FasterXML/jackson-databind/pull/6129

### CVE-2026-68497 (CWE-770) in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
jackson-databind - Allocation of Resources Without Limits or Throttling
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-68497?component-type=maven&component-name=com.fasterxml.jackson.core%2Fjackson-databind&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-68497
* https://github.com/FasterXML/jackson-databind/pull/6127

## Security

* #69: Fixed vulnerability CVE-2026-19032 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`
* #70: Fixed vulnerability CVE-2026-68497 in dependency `com.fasterxml.jackson.core:jackson-databind:jar:2.22.1:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.0.5`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.12` to `3.0.0`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.2` to `1.7.3`
* Updated `com.exasol:test-db-builder-java:4.0.1` to `4.0.2`
* Updated `com.exasol:udf-debugging-java:0.6.18` to `0.6.20`
* Updated `com.exasol:virtual-schema-common-jdbc:14.0.4` to `14.0.5`
* Updated `com.fasterxml.jackson.core:jackson-core:2.22.1` to `2.22.2`
* Updated `com.fasterxml.jackson.core:jackson-databind:2.22.1` to `2.22.2`
* Updated `com.google.cloud:google-cloud-bigquery:2.68.0` to `2.69.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.4` to `6.1.3`
