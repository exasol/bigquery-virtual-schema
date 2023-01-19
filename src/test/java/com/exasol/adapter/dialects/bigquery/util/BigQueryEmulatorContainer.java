package com.exasol.adapter.dialects.bigquery.util;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.util.logging.Logger;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.exasol.bucketfs.Bucket;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

class BigQueryEmulatorContainer extends GenericContainer<BigQueryEmulatorContainer> implements BigQueryTestSetup {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTestSetup.class.getName());
    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName
            .parse("ghcr.io/goccy/bigquery-emulator:0.2.12");
    private static final int PORT = 9050;
    private static final String PROJECT_ID = "test";
    private Path dataYaml;

    BigQueryEmulatorContainer(final Path dataYaml) {
        this(DEFAULT_IMAGE_NAME);
        this.dataYaml = dataYaml;
    }

    BigQueryEmulatorContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        withExposedPorts(PORT);
        final String dataOption = dataYaml == null ? "" : " --data-from-yaml" + dataYaml.toAbsolutePath().toString();
        withCommand("/bin/bigquery-emulator --project=" + PROJECT_ID + " --port=" + PORT + dataOption);
        waitingFor(Wait.forLogMessage("^\\[bigquery-emulator\\] REST server listening at 0\\.0\\.0\\.0:" + PORT + ".*",
                1));
        withStartupTimeout(Duration.ofSeconds(10));
        withStartupAttempts(1);
        withReuse(false);
    }

    @Override
    public String getProjectId() {
        return PROJECT_ID;
    }

    private String getUrl() {
        return "http://" + getHost() + ":" + getMappedPort(PORT);
    }

    @Override
    public BigQuery getClient() {
        final String url = getUrl();
        final String projectId = getProjectId();
        LOGGER.fine("Connecting to bigquery at " + url + " with project id '" + projectId + "'");
        return BigQueryOptions.newBuilder().setHost(url).setLocation(url).setProjectId(projectId).build().getService();
    }

    @Override
    public InetSocketAddress getServiceAddress() {
        return new InetSocketAddress(getHost(), getMappedPort(PORT));
    }

    @Override
    public String getJdbcUrl(final Bucket bucket, final InetSocketAddress serviceAddress) {
        final String hostAndPort = serviceAddress.toString();
        final String url = "http://" + hostAndPort;
        return "jdbc:bigquery://" + url + ";ProjectId=" + getProjectId() //
                + ";RootURL=" + url //
                + ";OAuthType=2;OAuthAccessToken=dummy-token";
    }

    @Override
    public void close() {
        this.stop();
    }
}