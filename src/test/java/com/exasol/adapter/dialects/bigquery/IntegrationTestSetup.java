package com.exasol.adapter.dialects.bigquery;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.exasol.adapter.dialects.bigquery.testcontainer.BigQueryEmulatorContainer;
import com.exasol.adapter.dialects.bigquery.util.BucketFsFolder;
import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.DatabaseObject;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.exasoltestsetup.*;
import com.exasol.udfdebugging.UdfTestSetup;
import com.google.cloud.bigquery.BigQuery;

public class IntegrationTestSetup implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTestSetup.class.getName());
    private static final String BUCKETFS_ROOT_PATH = "/buckets/bfsdefault/default/";

    private static final String ADAPTER_JAR = "virtual-schema-dist-9.0.5-bigquery-2.0.2.jar";
    public static final Path ADAPTER_JAR_LOCAL_PATH = Path.of("target", ADAPTER_JAR);
    private final ExasolTestSetup exasolTestSetup = new ExasolTestSetupFactory(
            Path.of("cloudSetup/generated/testConfig.json")).getTestSetup();

    private final BigQueryEmulatorContainer bigQueryContainer;
    private final Connection connection;
    private final Statement statement;
    private final ExasolObjectFactory exasolObjectFactory;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private final Bucket bucket;
    private final List<DatabaseObject> createdObjects = new LinkedList<>();
    private final UdfTestSetup udfTestSetup;

    private IntegrationTestSetup(final BigQueryEmulatorContainer bigQueryContainer)
            throws SQLException, BucketAccessException, TimeoutException, IOException, URISyntaxException {
        this.bigQueryContainer = bigQueryContainer;
        this.connection = this.exasolTestSetup.createConnection();
        this.statement = this.connection.createStatement();
        this.statement.executeUpdate("ALTER SESSION SET QUERY_CACHE = 'OFF';");
        this.bucket = this.exasolTestSetup.getDefaultBucket();
        this.udfTestSetup = new UdfTestSetup(this.exasolTestSetup, this.connection);
        final List<String> jvmOptions = new ArrayList<>(Arrays.asList(this.udfTestSetup.getJvmOptions()));
        this.exasolObjectFactory = new ExasolObjectFactory(this.connection,
                ExasolObjectConfiguration.builder().withJvmOptions(jvmOptions.toArray(String[]::new)).build());
        final ExasolSchema adapterSchema = this.exasolObjectFactory.createSchema("ADAPTER");
        this.connectionDefinition = createConnectionDefinition();
        this.adapterScript = createAdapterScript(adapterSchema);
    }

    public static IntegrationTestSetup create(final Path bigQueryDataYaml) {
        final BigQueryEmulatorContainer bigQueryContainer = new BigQueryEmulatorContainer(bigQueryDataYaml);
        bigQueryContainer.start();
        try {
            return new IntegrationTestSetup(bigQueryContainer);
        } catch (SQLException | BucketAccessException | TimeoutException | IOException | URISyntaxException exception) {
            throw new IllegalStateException("Failed to create test setup: " + exception.getMessage(), exception);
        }
    }

    public InetSocketAddress makeLocalServiceAvailableInExasol(final int port) {
        final ServiceAddress serviceAddress = this.exasolTestSetup.makeLocalTcpServiceAccessibleFromDatabase(port);
        return new InetSocketAddress(serviceAddress.getHostName(), serviceAddress.getPort());
    }

    public ConnectionDefinition createConnectionDefinition() {
        final String hostAndPort = this.exasolTestSetup
                .makeTcpServiceAccessibleFromDatabase(bigQueryContainer.getServiceAddress()).toString();
        final String url = "http://" + hostAndPort;
        final String jdbcUrl = "jdbc:bigquery://" + url + ";ProjectId=" + bigQueryContainer.getProjectId() //
                + ";RootURL=" + url //
                + ";OAuthType=2;OAuthAccessToken=a25c7cfd36214f94a79d" //
                + ";MaxResults=1000;MetaDataFetchThreadCount=32";
        return this.exasolObjectFactory.createConnectionDefinition("BIGQUERY_CONNECTION", jdbcUrl, "", "");
    }

    AdapterScript createAdapterScript(final ExasolSchema adapterSchema)
            throws BucketAccessException, TimeoutException, IOException, URISyntaxException {
        this.bucket.uploadFile(ADAPTER_JAR_LOCAL_PATH, ADAPTER_JAR);
        return adapterSchema.createAdapterScriptBuilder("ADAPTER_SCRIPT_BIGQUERY")
                .bucketFsContent("com.exasol.adapter.RequestDispatcher", getAdapterJarsInBucketFs())
                .language(AdapterScript.Language.JAVA).build();
    }

    @NotNull
    private String[] getAdapterJarsInBucketFs()
            throws IOException, URISyntaxException, BucketAccessException, TimeoutException {
        final List<String> jars = new ArrayList<>();
        jars.add(BUCKETFS_ROOT_PATH + ADAPTER_JAR);
        jars.addAll(uploadJdbcDriverToBucketFs());
        return jars.toArray(new String[0]);
    }

    private List<String> uploadJdbcDriverToBucketFs()
            throws IOException, URISyntaxException, BucketAccessException, TimeoutException {
        final JdbcDriver jdbcDriver = new JdbcDriver().withSourceUrl(
                "https://storage.googleapis.com/simba-bq-release/jdbc/SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip")
                .withLocalFolder("target");

        final ZipDownloader extracting = ZipDownloader.extracting(jdbcDriver.getDownloadUrl(),
                jdbcDriver.getLocalCopy());

        if (!extracting.localCopyExists()) {
            extracting.download();
        }

        final BucketFsFolder bucketFs = new BucketFsFolder(exasolTestSetup.getDefaultBucket(),
                jdbcDriver.getBucketFsFolder());

        final List<Path> jarPaths = extracting.inventory("*.jar");
        for (final Path file : jarPaths) {
            final String target = jdbcDriver.getPathInBucketFs(file);
            if (!bucketFs.contains(file)) {
                LOGGER.finest("Uploading to bucketfs: " + target);
                exasolTestSetup.getDefaultBucket().uploadFile(file, target);
            }
        }

        return jarPaths.stream().map(path -> path.getFileName().toString())
                .map(fileName -> BUCKETFS_ROOT_PATH + jdbcDriver.getBucketFsFolder() + "/" + fileName)
                .collect(toList());
    }

    @Override
    public void close() throws Exception {
        this.bigQueryContainer.stop();
        this.udfTestSetup.close();
        this.statement.close();
        this.connection.close();
        this.exasolTestSetup.close();
    }

    protected VirtualSchema createVirtualSchema(final String schemaName) {
        return createVirtualSchema(schemaName, this.connectionDefinition);
    }

    protected VirtualSchema createVirtualSchema(final String schemaName, final ConnectionDefinition connection) {
        final VirtualSchema virtualSchema = getPreconfiguredVirtualSchemaBuilder(schemaName)
                .connectionDefinition(connection)//
                .properties(getVirtualSchemaProperties()).build();
        this.createdObjects.add(virtualSchema);
        return virtualSchema;
    }

    @NotNull
    private Map<String, String> getVirtualSchemaProperties() {
        final Map<String, String> properties = new HashMap<>();
        final String debugProperty = System.getProperty("test.debug", "");
        final String profileProperty = System.getProperty("test.jprofiler", "");
        if (!debugProperty.isBlank() || !profileProperty.isBlank()) {
            properties.put("MAX_PARALLEL_UDFS", "1");
        }
        if (System.getProperty("test.vs-logs", "false").equals("true")) {
            properties.put("DEBUG_ADDRESS", "127.0.0.1:3001");
            properties.put("LOG_LEVEL", "ALL");
        }
        return properties;
    }

    public void dropCreatedObjects() {
        for (final DatabaseObject createdObject : this.createdObjects) {
            createdObject.drop();
        }
        this.createdObjects.clear();
    }

    public Connection getConnection() {
        return connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public Bucket getBucket() {
        return this.bucket;
    }

    public BigQuery getBigQueryClient() {
        return this.bigQueryContainer.getClient();
    }

    public ExasolObjectFactory getExasolObjectFactory() {
        return this.exasolObjectFactory;
    }

    public VirtualSchema.Builder getPreconfiguredVirtualSchemaBuilder(final String schemaName) {
        return this.exasolObjectFactory.createVirtualSchemaBuilder(schemaName)
                .connectionDefinition(this.connectionDefinition).adapterScript(this.adapterScript);
    }
}
