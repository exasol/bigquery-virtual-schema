# Virtual Schema for BigQuery 3.0.0, released 2024-??-??

Code name:

## Summary

The behaviour when it comes to character sets is now simplified,
The target char set is now always UTF-8.
The `IMPORT_DATA_TYPES` property (and value `FROM_RESULT_SET`) are now deprecated (change in vs-common-jdbc):
An exception will be thrown when users use`FROM_RESULT_SET`. The exception message warns the user that the value is no longer supported and the property itself is also deprecated.

## Refactoring

* #22: Update tests to include Exasol V8/ Update to vsjdbc 12.0.0

## Dependency Updates

### Plugin Dependency Updates

* Updated `com.exasol:error-code-crawler-maven-plugin:2.0.0` to `2.0.1`
* Updated `com.exasol:project-keeper-maven-plugin:4.1.0` to `4.2.0`
