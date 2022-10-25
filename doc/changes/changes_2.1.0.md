# Virtual Schema for BigQuery 2.1.0, released 2022-??-??

Code name: Updated dependencies

## Summary

Updated dependencies to fix vulnerabilities and changed error code prefix from `VS-BIGQ` to `VSBIGQ`.

## Refactorings

* #11: Removed individual system property `test.vs-logs` and unconditional activation of `LOG_LEVEL=ALL` in favor of default system properties supported by [test-db-builder-java](https://github.com/exasol/test-db-builder-java), see [TDBJ User Guide](https://github.com/exasol/test-db-builder-java/blob/main/doc/user_guide/user_guide.md#debug-output).
* #13 Updated to latest version of virtual-schema-common-jdbc providing enhanced data type detection of result sets in import statements. However there is no additional benefit as BigQuery uses `SELECT FROM VALUES`.
* #10:  Changed error code prefix from `VS-BIGQ` to `VSBIGQ`.

## Fixed vulnerabilities

* #12: Fixed vulnerabilities
  * [com.google.protobuf:protobuf-java:jar:3.21.1](https://ossindex.sonatype.org/component/pkg:maven/com.google.protobuf/protobuf-java@3.21.1?utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1) in test
    * CVE-2022-3171, severity CWE-400: Uncontrolled Resource Consumption ('Resource Exhaustion') (7.5)
  * [com.fasterxml.jackson.core:jackson-databind:jar:2.13.3](https://ossindex.sonatype.org/component/pkg:maven/com.fasterxml.jackson.core/jackson-databind@2.13.3?utm_source=ossindex-client&utm_medium=integration&utm_content=1.8.1) in test
    * CVE-2022-42003, severity CWE-502: Deserialization of Untrusted Data (7.5)
    * CVE-2022-42004, severity CWE-502: Deserialization of Untrusted Data (7.5)

## Dependency Updates

### Compile Dependency Updates

* Updated `com.exasol:error-reporting-java:0.4.1` to `1.0.0`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.5` to `10.0.1`

### Test Dependency Updates

* Updated `com.exasol:hamcrest-resultset-matcher:1.5.1` to `1.5.2`
* Updated `com.exasol:test-db-builder-java:3.3.3` to `3.4.1`
* Updated `com.exasol:virtual-schema-common-jdbc:9.0.5` to `10.0.1`
* Added `com.fasterxml.jackson.core:jackson-databind:2.13.4.2`
* Updated `com.google.cloud:google-cloud-bigquery:2.14.0` to `2.17.1`
* Removed `io.grpc:grpc-core:1.48.0`
* Removed `io.netty:netty-common:4.1.79.Final`
* Updated `org.junit.jupiter:junit-jupiter:5.8.2` to `5.9.1`
* Updated `org.mockito:mockito-junit-jupiter:4.6.1` to `4.8.1`
* Updated `org.testcontainers:jdbc:1.17.3` to `1.17.5`
* Updated `org.testcontainers:junit-jupiter:1.17.3` to `1.17.5`

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:1.1.1` to `1.1.2`
* Updated `com.exasol:project-keeper-maven-plugin:2.5.0` to `2.8.0`
* Updated `org.apache.maven.plugins:maven-enforcer-plugin:3.0.0` to `3.1.0`
