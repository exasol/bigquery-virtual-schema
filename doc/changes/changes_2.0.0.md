# Big Query Virtual Schema 2.0.0, released 2021-03-??

Code name: Removed `SQL_DIALECT` property

## Summary

The `SQL_DIALECT` property used when executing a `CREATE VIRTUAL SCHEMA` from the Exasol database is obsolete from this version. Please, do not provide this property anymore.

## Features

* #3: Added new capabilities for scalar functions.

## Dependencies Updates

### Runtime Dependencies

* Updated `com.exasol:error-reporting-java:0.2.0` to `0.2.2`
* Updated `com.exasol:virtual-schema-common-jdbc:8.0.0` to `9.0.1`

### Test Dependencies

* Updated `org.junit.jupiter:junit-jupiter:5.7.0` to `5.7.1`
* Updated `org.mockito:mockito-junit-jupiter:3.6.28` to `3.7.7`

### Plugin Dependencies

* Added `com.exasol:error-code-crawler-maven-plugin:0.1.1`