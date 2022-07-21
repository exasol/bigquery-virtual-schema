package com.exasol.adapter.dialects.bigquery.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.TimeoutException;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ServiceAddress;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

public class GoogleCloudBigQuerySetup implements BigQueryTestSetup {
    private final GoogleCloudCredentials credentials;

    public GoogleCloudBigQuerySetup(final GoogleCloudCredentials credentials) {
        this.credentials = Objects.requireNonNull(credentials, "credentials");
    }

    @Override
    public BigQuery getClient() {
        return BigQueryOptions.newBuilder() //
                .setProjectId(getProjectId()) //
                .setCredentials(createGoogleCredentials()) //
                .build().getService();
    }

    private Credentials createGoogleCredentials() {
        try {
            return GoogleCredentials.fromStream(Files.newInputStream(credentials.privateKey));
        } catch (final IOException exception) {
            throw new UncheckedIOException("Failed to load credentials from " + credentials.privateKey, exception);
        }
    }

    @Override
    public ServiceAddress getServiceAddress() {
        return new ServiceAddress("www.googleapis.com", 443);
    }

    @Override
    public String getProjectId() {
        return "pin-dev-bigquery";
    }

    @Override
    public String getJdbcUrl(final Bucket bucket, final ServiceAddress serviceAddress) {
        final String url = "https://" + serviceAddress.getHostName() + ":" + serviceAddress.getPort();
        final String bucketFsCredentialsPath = uploadCredentials(bucket);
        return "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443;RootURL=" + url + ";ProjectId="
                + getProjectId() + ";OAuthType=0;OAuthServiceAcctEmail=" + credentials.serviceAccountEmail
                + ";OAuthPvtKeyPath=" + bucketFsCredentialsPath;
    }

    private String uploadCredentials(final Bucket bucket) {
        try {
            final String filename = credentials.privateKey.getFileName().toString();
            bucket.uploadFile(credentials.privateKey, filename);
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
