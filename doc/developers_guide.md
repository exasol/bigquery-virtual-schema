# Developers Guide

This guide contains information for developers.

## Running Integration Tests Against Google Cloud

Integration tests are prepared to use a local [bigquery-emulator](https://github.com/goccy/bigquery-emulator), but the emulator does not yet support all required features. Until this is finished it's only possible to run integration tests against BigQuery in Google Cloud:

1. Login to Google Cloud
2. Go to "IAM > Service Accounts" and create a Service Account and download the private key as JSON file. Store JSON file as `google-service-account-key.json`.
3. Go to "IAM > Roles" and create a new role with permissions `bigquery.datasets.create` and `bigquery.jobs.create`. Set "Role launch stage" to "General Availability".
4. Go to "IAM > IAM", click "Grant Access", select the service account as principal, select the role and click "Save".
5. Create file `test.properties` with the following content:

    ```properties
    googleProjectId = google-project-id
    serviceAccountEmail = your.google.account@example.com
    privateKeyPath = /path/to/google-service-account-key.json

    udfLoggingEnabled = true
    ```
    
If file `test.properties` or one of `googleProjectId`, `serviceAccountEmail`, or `privateKeyPath` is missing, then integration tests will be skipped.

When `udfLoggingEnabled` is set to `true`, UDF logs will be written to `target/udf-logs/*.txt`.

## Running Integration Tests Against BigQuery Emulator

As long as the emulator is not yet ready you need to manually enable it.

1. Remove `test.properties` or one of the properties `googleProjectId`, `serviceAccountEmail`, or `privateKeyPath` in the file.
2. Remove the `assumeTrue` call from `BigQueryVirtualSchemaIT.beforeAll()`.

For manual testing you can start the emulator with the following command:

```sh
docker run --publish 9050:9050 --publish 9060:9060 ghcr.io/goccy/bigquery-emulator:0.6.6 --project=myProject --port=9050 --grpc-port=9060 --log-level=debug
```
