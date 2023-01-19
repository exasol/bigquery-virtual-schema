package com.exasol.adapter.dialects.bigquery.util;

import java.net.InetSocketAddress;
import java.nio.file.Paths;

import com.exasol.bucketfs.Bucket;
import com.google.cloud.bigquery.BigQuery;

public interface BigQueryTestSetup extends AutoCloseable {

    static BigQueryTestSetup createLocalSetup() {
        return new BigQueryEmulatorContainer(Paths.get("src/test/resources/bigquery-data.yaml"));
    }

    static BigQueryTestSetup createGoogleCloudSetup(final TestConfig config) {
        return new GoogleCloudBigQuerySetup(config);
    }

    BigQuery getClient();

    InetSocketAddress getServiceAddress();

    String getProjectId();

    String getJdbcUrl(Bucket bucket, InetSocketAddress serviceAddress);

    void start();

    @Override
    void close();
}
