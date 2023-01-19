package com.exasol.adapter.dialects.bigquery.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

public class GoogleCloudBigQuerySetup implements BigQueryTestSetup {
    private final TestConfig config;

    public GoogleCloudBigQuerySetup(final TestConfig config) {
        this.config = config;
    }

    @Override
    public BigQuery getClient() {
        return BigQueryOptions.newBuilder() //
                .setProjectId(getProjectId()) //
                .setCredentials(createGoogleCredentials()) //
                .build().getService();
    }

    private Credentials createGoogleCredentials() {
        final Path privateKey = config.getGoogleCloudCredentials().privateKey;
        try {
            return GoogleCredentials.fromStream(Files.newInputStream(privateKey));
        } catch (final IOException exception) {
            throw new UncheckedIOException("Failed to load credentials from " + privateKey, exception);
        }
    }

    @Override
    public InetSocketAddress getServiceAddress() {
        return new InetSocketAddress("www.googleapis.com", 443);
    }

    @Override
    public String getProjectId() {
        return config.getGoogleProjectId();
    }

    @Override
    public String getJdbcUrl(final Bucket bucket, final InetSocketAddress serviceAddress) {
        final String url = "https://" + serviceAddress.getHostName() + ":" + serviceAddress.getPort();
        final String bucketFsCredentialsPath = uploadCredentials(bucket);
        return "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;RootURL=" + url + ";ProjectId="
                + getProjectId() + ";OAuthType=0;OAuthServiceAcctEmail="
                + config.getGoogleCloudCredentials().serviceAccountEmail + ";OAuthPvtKeyPath="
                + bucketFsCredentialsPath;
    }

    private String uploadCredentials(final Bucket bucket) {
        final Path privateKey = config.getGoogleCloudCredentials().privateKey;
        try {
            final String filename = privateKey.getFileName().toString();
            bucket.uploadFile(privateKey, filename);
            return IntegrationTestSetup.BUCKETFS_ROOT_PATH + filename;
        } catch (FileNotFoundException | BucketAccessException | TimeoutException exception) {
            throw new IllegalStateException("Failed to upload google cloud credentials", exception);
        }
    }

    public static class GoogleCloudCredentials {
        private final String serviceAccountEmail;
        private final Path privateKey;

        public GoogleCloudCredentials(final String serviceAccountEmail, final Path privateKey) {
            this.serviceAccountEmail = serviceAccountEmail;
            this.privateKey = privateKey;
        }
    }

    @Override
    public void start() {
        // ignore
    }

    @Override
    public void close() {
        // nothing to do
    }
}
