# Virtual Schema for BigQuery 4.0.2, released 2026-??-??

Code name: Fixed vulnerability CVE-2026-86231 in com.github.mwiede:jsch:jar:2.28.4:test

## Summary

This release fixes the following vulnerability:

### CVE-2026-86231 (CWE-298) in dependency `com.github.mwiede:jsch:jar:2.28.4:test`
A security flaw has been discovered in mwiede jsch up to 2.28.5. Affected is the function getRevokedKeys of the file src/main/java/com/jcraft/jsch/KnownHosts.java. Performing a manipulation of the argument known_hosts results in improper check for certificate revocation. The attack is possible to be carried out remotely. The attack is considered to have high complexity. The exploitability is told to be difficult. The exploit has been released to the public and may be used for attacks. Upgrading to version 2.28.6 is able to address this issue. The patch is named 194a2f76a5c0f1c3f778565be3fd66bcafc42d23. You should upgrade the affected component.
#### References
* https://guide.sonatype.com/vulnerability/CVE-2026-86231?component-type=maven&component-name=com.github.mwiede%2Fjsch&utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1
* http://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2026-86231
* https://github.com/mwiede/jsch/issues/1091
* https://github.com/mwiede/jsch/pull/1098
* https://github.com/mwiede/jsch/releases/tag/jsch-2.28.6

## Security

* #72: Fixed vulnerability CVE-2026-86231 in dependency `com.github.mwiede:jsch:jar:2.28.4:test`

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
* Updated `com.google.cloud:google-cloud-bigquery:2.68.0` to `2.71.0`
* Updated `org.junit.jupiter:junit-jupiter-params:5.14.4` to `6.1.3`

### Plugin Dependency Updates

* Updated `com.exasol:project-keeper-maven-plugin:5.7.4` to `5.7.5`
