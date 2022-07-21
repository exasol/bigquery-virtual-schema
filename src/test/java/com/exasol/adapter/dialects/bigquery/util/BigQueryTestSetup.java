package com.exasol.adapter.dialects.bigquery.util;

import java.nio.file.Paths;

import com.exasol.bucketfs.Bucket;
import com.exasol.exasoltestsetup.ServiceAddress;
import com.google.cloud.bigquery.BigQuery;

public interface BigQueryTestSetup {

    public static BigQueryTestSetup createLocalSetup() {
        return new BigQueryEmulatorContainer(Paths.get("src/test/resources/bigquery-data.yaml"));
    }

    public static BigQueryTestSetup createGoogleCloudSetup(final TestConfig config) {
        return new GoogleCloudBigQuerySetup(config.getGoogleCloudCredentials());
    }

    void close();

    BigQuery getClient();

    ServiceAddress getServiceAddress();

    String getProjectId();

    String getJdbcUrl(Bucket bucket, ServiceAddress serviceAddress);

    void start();
}