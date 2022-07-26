package com.exasol.adapter.dialects.bigquery.util;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import com.exasol.adapter.dialects.bigquery.util.GoogleCloudBigQuerySetup.GoogleCloudCredentials;

public class TestConfig {
    private static final Logger LOG = Logger.getLogger(TestConfig.class.getName());
    private static final Path CONFIG_FILE = Paths.get("test.properties");

    private final Properties properties;

    TestConfig(final Properties properties) {
        this.properties = properties;
    }

    public static TestConfig read() {
        if (Files.exists(CONFIG_FILE)) {
            return read(CONFIG_FILE);
        } else {
            return new TestConfig(new Properties());
        }
    }

    static TestConfig read(final Path configFile) {
        final Path file = configFile.normalize();
        return new TestConfig(loadProperties(file));
    }

    private static Properties loadProperties(final Path configFile) {
        if (!Files.exists(configFile)) {
            throw new IllegalStateException("Config file not found at '" + configFile + "'");
        }
        LOG.info("Reading config file from " + configFile);
        try (InputStream stream = Files.newInputStream(configFile)) {
            final Properties props = new Properties();
            props.load(stream);
            return props;
        } catch (final IOException e) {
            throw new UncheckedIOException("Error reading config file " + configFile, e);
        }
    }

    public boolean hasGoogleCloudCredentials() {
        return getOptionalValue("serviceAccountEmail").isPresent();
    }

    public GoogleCloudCredentials getGoogleCloudCredentials() {
        final String serviceAccountEmail = getMandatoryValue("serviceAccountEmail");
        final Path privateKey = Paths.get(getMandatoryValue("privateKeyPath")).toAbsolutePath();
        if (!Files.exists(privateKey)) {
            throw new IllegalArgumentException("Private key does not exist at " + privateKey);
        }
        return new GoogleCloudCredentials(serviceAccountEmail, privateKey);
    }

    public String getGoogleProjectId() {
        return getMandatoryValue("googleProjectId");
    }

    public boolean isUdfLoggingEnabled() {
        return getOptionalValue("udfLoggingEnabled") //
                .filter(v -> v.equalsIgnoreCase("true")) //
                .isPresent();
    }

    public String getMandatoryValue(final String param) {
        return getOptionalValue(param)
                .orElseThrow(() -> new IllegalStateException("Property '" + param + "' not found in config file"));
    }

    public Optional<String> getOptionalValue(final String param) {
        return Optional.ofNullable(this.properties.getProperty(param));
    }

}
