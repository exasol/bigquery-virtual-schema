package com.exasol.adapter.dialects.bigquery.util;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.DatabaseObject;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;
import com.exasol.udfdebugging.UdfTestSetup;
import com.google.cloud.bigquery.BigQuery;

public class IntegrationTestSetup implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTestSetup.class.getName());
    private static final String ADAPTER_JAR = "virtual-schema-dist-12.0.0-bigquery-3.0.5.jar";
    public static final String BUCKETFS_ROOT_PATH = "/buckets/bfsdefault/default/";
    public static final Path ADAPTER_JAR_LOCAL_PATH = Path.of("target", ADAPTER_JAR);

    private final BigQueryTestSetup bigQueryTestSetup;
    private final ExasolTestSetup exasolTestSetup;
    private final Connection connection;
    private final Statement statement;
    private final ExasolObjectFactory exasolObjectFactory;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private final List<DatabaseObject> createdObjects = new LinkedList<>();
    private final UdfTestSetup udfTestSetup;
    private final BigQueryDatasetFixture bigQueryDataset;

    private IntegrationTestSetup(final BigQueryTestSetup bigQueryTestSetup, final ExasolTestSetup exasolTestSetup)
            throws SQLException, BucketAccessException, TimeoutException, IOException {
        this.bigQueryTestSetup = bigQueryTestSetup;
        this.bigQueryDataset = BigQueryDatasetFixture.create(bigQueryTestSetup.getClient(),
                bigQueryTestSetup.getProjectId());
        this.exasolTestSetup = exasolTestSetup;
        this.connection = this.exasolTestSetup.createConnection();
        this.statement = this.connection.createStatement();
        this.statement.executeUpdate("ALTER SESSION SET QUERY_CACHE = 'OFF';");
        this.udfTestSetup = new UdfTestSetup(this.exasolTestSetup, this.connection);
        final List<String> jvmOptions = new ArrayList<>(Arrays.asList(this.udfTestSetup.getJvmOptions()));
        this.exasolObjectFactory = new ExasolObjectFactory(this.connection,
                ExasolObjectConfiguration.builder().withJvmOptions(jvmOptions.toArray(String[]::new)).build());
        final ExasolSchema adapterSchema = this.exasolObjectFactory.createSchema("ADAPTER");
        this.connectionDefinition = createConnectionDefinition();
        this.adapterScript = createAdapterScript(adapterSchema);
    }

    public static IntegrationTestSetup create(final TestConfig config) {
        if (config.isUdfLoggingEnabled()) {
            System.setProperty("test.udf-logs", "true");
        }
        final BigQueryTestSetup bigQueryTestSetup = createBigQueryTestSetup(config);
        assertNotNull(bigQueryTestSetup.getClient());
        bigQueryTestSetup.start();
        final ExasolTestSetup exasolTestSetup = new ExasolTestSetupFactory(
                Path.of("cloudSetup/generated/testConfig.json")).getTestSetup();
        try {
            return new IntegrationTestSetup(bigQueryTestSetup, exasolTestSetup);
        } catch (SQLException | BucketAccessException | TimeoutException | IOException exception) {
            throw new IllegalStateException("Failed to create test setup: " + exception.getMessage(), exception);
        }
    }

    private static BigQueryTestSetup createBigQueryTestSetup(final TestConfig config) {
        if (config.hasGoogleCloudCredentials()) {
            LOGGER.info("Using Google Cloud BigQuery setup");
            return BigQueryTestSetup.createGoogleCloudSetup(config);
        } else {
            LOGGER.info("Using local BigQuery setup");
            return BigQueryTestSetup.createLocalSetup();
        }
    }

    public ConnectionDefinition createConnectionDefinition() {
        final InetSocketAddress bigQueryServiceAddress = this.exasolTestSetup
                .makeTcpServiceAccessibleFromDatabase(bigQueryTestSetup.getServiceAddress());
        final String jdbcUrl = this.bigQueryTestSetup.getJdbcUrl(getBucket(), bigQueryServiceAddress);
        LOGGER.fine(() -> "Creating JDBC connection to URL " + jdbcUrl);
        return this.exasolObjectFactory.createConnectionDefinition("BIGQUERY_CONNECTION", jdbcUrl, "", "");
    }

    AdapterScript createAdapterScript(final ExasolSchema adapterSchema)
            throws FileNotFoundException, BucketAccessException, TimeoutException {
        getBucket().uploadFile(ADAPTER_JAR_LOCAL_PATH, ADAPTER_JAR);
        return adapterSchema.createAdapterScriptBuilder("ADAPTER_SCRIPT_BIGQUERY")
                .bucketFsContent("com.exasol.adapter.RequestDispatcher", getAdapterJarsInBucketFs())
                .language(AdapterScript.Language.JAVA).build();
    }

    @NotNull
    private String[] getAdapterJarsInBucketFs() {
        final JdbcDriverProvider uploader = new JdbcDriverProvider(getBucket());
        final List<String> jarFiles = uploader.uploadJdbcDriverToBucketFs(
                "https://storage.googleapis.com/simba-bq-release/jdbc/SimbaJDBCDriverforGoogleBigQuery42_1.6.2.1003.zip");
        final List<String> jars = new ArrayList<>();
        jars.add(BUCKETFS_ROOT_PATH + ADAPTER_JAR);
        jars.addAll(jarFiles);
        return jars.toArray(new String[0]);
    }

    @Override
    public void close() throws Exception {
        this.bigQueryDataset.close();
        this.bigQueryTestSetup.close();
        this.udfTestSetup.close();
        this.statement.close();
        this.connection.close();
        this.exasolTestSetup.close();
    }

    public VirtualSchema createVirtualSchema(final String schemaName) {
        final VirtualSchema virtualSchema = this.exasolObjectFactory.createVirtualSchemaBuilder(schemaName)
                .connectionDefinition(this.connectionDefinition) //
                .adapterScript(this.adapterScript) //
                .sourceSchemaName(this.bigQueryDataset.getDatasetId().getDataset()) //
                .properties(getVirtualSchemaProperties()).build();
        this.createdObjects.add(virtualSchema);
        return virtualSchema;
    }

    /**
     * {@link VirtualSchema} constructor will automatically set properties {@code DEBUG_ADDRESS} and {@code LOG_LEVEL}
     * if the corresponding system properties are set, see <a href=
     * "https://github.com/exasol/test-db-builder-java/blob/main/doc/user_guide/user_guide.md#debug-output">test-db-builder-java/user
     * guide</a>.
     * <ul>
     * <li>{@code com.exasol.virtualschema.debug.host}</li>
     * <li>{@code com.exasol.virtualschema.debug.port}</li>
     * <li>{@code com.exasol.virtualschema.debug.level}</li>
     * </ul>
     *
     * @return additional individual properties for virtual schema
     */
    private Map<String, String> getVirtualSchemaProperties() {
        final Map<String, String> properties = new HashMap<>();
        properties.put("CATALOG_NAME", this.bigQueryDataset.getDatasetId().getProject());
        final String debugProperty = System.getProperty("test.debug", "");
        final String profileProperty = System.getProperty("test.jprofiler", "");
        if (!debugProperty.isBlank() || !profileProperty.isBlank()) {
            properties.put("MAX_PARALLEL_UDFS", "1");
        }
        return properties;
    }

    public void dropCreatedObjects() {
        for (final DatabaseObject createdObject : this.createdObjects) {
            createdObject.drop();
        }
        this.createdObjects.clear();
        this.bigQueryDataset.dropCreatedObjects();
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Statement getStatement() {
        return this.statement;
    }

    public Bucket getBucket() {
        return this.exasolTestSetup.getDefaultBucket();
    }

    public BigQuery getBigQueryClient() {
        return this.bigQueryTestSetup.getClient();
    }

    public ExasolObjectFactory getExasolObjectFactory() {
        return this.exasolObjectFactory;
    }

    public BigQueryDatasetFixture bigQueryDataset() {
        return this.bigQueryDataset;
    }
}
