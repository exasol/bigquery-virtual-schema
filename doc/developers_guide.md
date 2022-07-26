# Developers Guide

This guide contains information for developers.

## Running Integration Tests Against Google Cloud

Integration tests are prepared to use a local [bigquery-emulator](https://github.com/goccy/bigquery-emulator), but the emulator does not yet support all required features. Until this is finished it's only possible to run integration tests against BigQuery in Google Cloud:

1. Login to Google Cloud, create a Service Account and download the private key as JSON file.
2. Create file `test.properties` with the following content:

    ```properties
    googleProjectId = google-project-id
    serviceAccountEmail = your.google.account@example.com
    privateKeyPath = /path/to/private-key.json

    udfLoggingEnabled = true
    ```
    
If file `test.properties` or one of `googleProjectId`, `serviceAccountEmail`, or `privateKeyPath` is missing, then integration tests will be skipped.

When `udfLoggingEnabled` is set to `true`, UDF logs will be written to `target/udf-logs/*.txt`.
