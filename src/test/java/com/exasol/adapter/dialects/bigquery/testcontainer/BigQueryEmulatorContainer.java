package com.exasol.adapter.dialects.bigquery.testcontainer;

import java.nio.file.Path;
import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

public class BigQueryEmulatorContainer extends GenericContainer<BigQueryEmulatorContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("ghcr.io/goccy/bigquery-emulator");
    private static final int PORT = 9050;
    private static final String PROJECT_ID = "test";
    private Path dataYaml;

    public BigQueryEmulatorContainer(final Path dataYaml) {
        this(DEFAULT_IMAGE_NAME);
        this.dataYaml = dataYaml;
    }

    public BigQueryEmulatorContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        withExposedPorts(PORT);
        final String dataOption = dataYaml == null ? "" : " --data-from-yaml" + dataYaml.toAbsolutePath().toString();
        withCommand("/bin/sh", "-c", "/bin/bigquery-emulator --project=" + PROJECT_ID + " --port=" + PORT + dataOption);
        waitingFor(Wait.forLogMessage("^\\[bigquery-emulator\\] listening at 0\\.0\\.0\\.0:" + PORT + ".*", 1));
        withStartupTimeout(Duration.ofSeconds(10));
        withStartupAttempts(1);
        withReuse(false);
    }

    public String getProjectId() {
        return PROJECT_ID;
    }

    private int getEmulatorPort() {
        return getMappedPort(PORT);
    }

    public String getBigQueryJdbcUrl() {
        final String url = getUrl();
        return "jdbc:bigquery://" + url + ";ProjectId=" + getProjectId() //
                + ";RootURL=" + url //
                + ";OAuthType=2;OAuthAccessToken=a25c7cfd36214f94a79d" //
                + ";MaxResults=1000;MetaDataFetchThreadCount=32";
    }

    private String getUrl() {
        return "http://" + getHost() + ":" + getEmulatorPort();
        // return "http://localhost:9050";
    }

    public BigQuery getClient() {
        return BigQueryOptions.newBuilder().setHost(getUrl()).setProjectId(getProjectId()).build().getService();
    }

    @Override
    public void close() {

    }
}