# Developers Guide

This guide contains information for developers.

## Running Integration Tests Against Google Cloud

Integration tests are prepared to use a local [bigquery-emulator](https://github.com/goccy/bigquery-emulator), but the project does not yet support all required features. Until this is finished you can run integration tests against BigQuery in Google Cloud:

1. Login to Google Cloud, create a Service Account and download the private key as JSON file.
2. Create file `test.properties` with the following content:

    ```properties
    serviceAccountEmail = your.google.account@example.com
    privateKeyPath = /path/to/private-key.json
    googleProjectId = google-project-id
    ```
    If file `test.properties` or any of it's entries is missing, then integration tests will use the local local bigquery-emulator by default, which as stated before does not support all required features, yet.
