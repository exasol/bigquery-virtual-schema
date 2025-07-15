# Virtual Schema for BigQuery 3.0.6, released 2025-??-??

Code name: Fixed vulnerability CVE-2025-48924 in org.apache.commons:commons-lang3:jar:3.16.0:test

## Summary

This release fixes the following vulnerability:

### CVE-2025-48924 (CWE-674) in dependency `org.apache.commons:commons-lang3:jar:3.16.0:test`
Uncontrolled Recursion vulnerability in Apache Commons Lang.

This issue affects Apache Commons Lang: Starting withÂ commons-lang:commons-langÂ 2.0 to 2.6, and, from org.apache.commons:commons-lang3 3.0 beforeÂ 3.18.0.

The methods ClassUtils.getClass(...) can throwÂ StackOverflowError on very long inputs. Because an Error is usually not handled by applications and libraries, a 
StackOverflowError couldÂ cause an application to stop.

Users are recommended to upgrade to version 3.18.0, which fixes the issue.
#### References
* https://ossindex.sonatype.org/vulnerability/CVE-2025-48924?component-type=maven&component-name=org.apache.commons%2Fcommons-lang3&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2025-48924
* https://github.com/advisories/GHSA-j288-q9x7-2f5v

## Security

* #53: Fixed vulnerability CVE-2025-48924 in dependency `org.apache.commons:commons-lang3:jar:3.16.0:test`

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:virtual-schema-common-jdbc:12.0.0` to `13.0.0`

### Test Dependency Updates

* Updated `com.exasol:exasol-test-setup-abstraction-java:2.1.7` to `2.1.8`
* Updated `com.exasol:hamcrest-resultset-matcher:1.7.0` to `1.7.1`
* Updated `com.exasol:test-db-builder-java:3.6.0` to `3.6.2`
* Updated `com.exasol:udf-debugging-java:0.6.15` to `0.6.16`
* Updated `com.exasol:virtual-schema-common-jdbc:12.0.0` to `13.0.0`
* Updated `com.google.cloud:google-cloud-bigquery:2.48.0` to `2.53.0`
* Updated `org.junit.jupiter:junit-jupiter:5.11.4` to `5.13.3`
* Updated `org.mockito:mockito-junit-jupiter:5.15.2` to `5.18.0`
* Updated `org.testcontainers:jdbc:1.20.4` to `1.21.3`
* Updated `org.testcontainers:junit-jupiter:1.20.4` to `1.21.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:4.5.0` to `5.2.2`
