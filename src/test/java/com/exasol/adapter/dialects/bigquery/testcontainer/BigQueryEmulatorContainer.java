package com.exasol.adapter.dialects.bigquery.testcontainer;

import java.time.Duration;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

public class BigQueryEmulatorContainer extends JdbcDatabaseContainer<BigQueryEmulatorContainer> {

    private static final DockerImageName DEFAULT_IMAGE_NAME = DockerImageName.parse("ghcr.io/goccy/bigquery-emulator");
    private static final int PORT = 9050;
    private static final String PROJECT_NAME = "big-query-test-project";

    public BigQueryEmulatorContainer() {
        this(DEFAULT_IMAGE_NAME);
    }

    public BigQueryEmulatorContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        dockerImageName.assertCompatibleWith(DEFAULT_IMAGE_NAME);
        withExposedPorts(PORT);
        //setWaitStrategy(new LogMessageWaitStrategy().withRegEx("\\[bigquery-emulator\\] listening at .*$"));
        withCommand("/bin/sh", "-c", "/bin/bigquery-emulator", "--project=" + PROJECT_NAME);
        withStartupTimeout(Duration.ofSeconds(30));
        withReuse(false);
    }

    public String getProjectName() {
        return PROJECT_NAME;
    }

    private int getEmulatorPort() {
        return getMappedPort(PORT);
    }

    @Override
    public String getDriverClassName() {
        return "com.simba.googlebigquery.jdbc.Driver";
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:bigquery://http://" + getHost() + ":" + getEmulatorPort() + "/;ProjectId=" + getProjectName()
                + ";OAuthType=[AuthValue]";
    }

    @Override
    public String getUsername() {
        return "user";
    }

    @Override
    public String getPassword() {
        return "pass";
    }

    @Override
    protected String getTestQueryString() {
        return "select 1";
    }

    @Override
    public void close() {

    }
}