package com.exasol.adapter.dialects.bigquery;

import static java.util.stream.Collectors.toList;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.exasol.adapter.dialects.bigquery.util.BucketFsFolder;
import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;
import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.DatabaseObject;
import com.exasol.dbbuilder.dialects.exasol.*;
import com.exasol.exasoltestsetup.*;
import com.exasol.udfdebugging.UdfTestSetup;

import jakarta.json.*;

public class IntegrationTestSetup implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTestSetup.class.getName());
    private static final String BUCKETFS_ROOT_PATH = "/buckets/bfsdefault/default/";

    private static final String ADAPTER_JAR = "virtual-schema-dist-9.0.5-bigquery-2.0.2.jar";
    public static final Path ADAPTER_JAR_LOCAL_PATH = Path.of("target", ADAPTER_JAR);
    private final ExasolTestSetup exasolTestSetup = new ExasolTestSetupFactory(
            Path.of("cloudSetup/generated/testConfig.json")).getTestSetup();

    private final Connection connection;
    private final Statement statement;
    private final ExasolObjectFactory exasolObjectFactory;
    private final AdapterScript adapterScript;
    private final ConnectionDefinition connectionDefinition;
    private final Bucket bucket;
    private final List<DatabaseObject> createdObjects = new LinkedList<>();
    private final UdfTestSetup udfTestSetup;

    public IntegrationTestSetup()
            throws SQLException, BucketAccessException, TimeoutException, IOException, URISyntaxException {
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

    public InetSocketAddress makeLocalServiceAvailableInExasol(final int port) {
        final ServiceAddress serviceAddress = this.exasolTestSetup.makeLocalTcpServiceAccessibleFromDatabase(port);
        return new InetSocketAddress(serviceAddress.getHostName(), serviceAddress.getPort());
    }

    private ConnectionDefinition createConnectionDefinition() {
        final JsonObjectBuilder configJson = getConnectionConfig();
        return createConnectionDefinition(configJson);
    }

    public JsonObjectBuilder getConnectionConfig() {
        final Optional<String> mfaToken = this.s3TestSetup.getMfaToken();
        final JsonObjectBuilder builder = Json.createObjectBuilder()//
                .add("awsEndpointOverride", getInDatabaseS3Address())//
                .add("awsRegion", this.s3TestSetup.getRegion())//
                .add("s3Bucket", this.s3BucketName)//
                .add("awsAccessKeyId", this.s3TestSetup.getUsername())//
                .add("awsSecretAccessKey", this.s3TestSetup.getPassword());
        mfaToken.ifPresent(s -> builder.add("awsSessionToken", s));
        return builder;
    }

    public ConnectionDefinition createConnectionDefinition(final JsonObjectBuilder details) {
        return this.exasolObjectFactory.createConnectionDefinition("S3_CONNECTION_" + System.currentTimeMillis(), "",
                "", toJson(details.build()));
    }

    private String toJson(final JsonObject configJson) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final JsonWriter writer = Json.createWriter(outputStream)) {
            writer.write(configJson);
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (final IOException exception) {
            throw new UncheckedIOException("Failed to serialize connection settings", exception);
        }
    }

    private String getInDatabaseS3Address() {
        final String s3Entrypoint = this.s3TestSetup.getEntrypoint();
        if (s3Entrypoint.contains(":")) {
            return this.exasolTestSetup.makeTcpServiceAccessibleFromDatabase(ServiceAddress.parse(s3Entrypoint))
                    .toString();
        } else {
            return s3Entrypoint;
        }
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

        final ZipDownloader downloader = new ZipDownloader(jdbcDriver.getDownloadUrl(), jdbcDriver.getLocalCopy());

        if (!downloader.localFolderExists()) {
            downloader.extractToLocalFolder();
        }

        final BucketFsFolder bucketFs = new BucketFsFolder(exasolTestSetup.getDefaultBucket(),
                jdbcDriver.getBucketFsFolder());
        // ensure there is no file with name we want to use for folder
        bucketFs.deleteFile();

        final List<Path> jarPaths = downloader.inventory("*.jar");
        for (final Path file : jarPaths) {
            final String target = jdbcDriver.getPathInBucketFs(file);
            if (bucketFs.contains(file)) {
                LOGGER.finest("File already available in bucketfs: " + target);
            } else {
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
        try {
            this.udfTestSetup.close();
            this.statement.close();
            this.connection.close();
            this.exasolTestSetup.close();
        } catch (final SQLException exception) {
            // at least we tried to close it
        }
    }

    protected VirtualSchema createVirtualSchema(final String schemaName, final String mapping) {
        return createVirtualSchema(schemaName, mapping, this.connectionDefinition);
    }

    protected VirtualSchema createVirtualSchema(final String schemaName, final String mapping,
            final ConnectionDefinition connection) {
        final VirtualSchema virtualSchema = getPreconfiguredVirtualSchemaBuilder(schemaName)
                .connectionDefinition(connection)//
                .properties(getVirtualSchemaProperties(mapping)).build();
        this.createdObjects.add(virtualSchema);
        return virtualSchema;
    }

    @NotNull
    private Map<String, String> getVirtualSchemaProperties(final String mapping) {
        final Map<String, String> properties = new HashMap<>(Map.of("MAPPING", mapping));
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

    public Bucket getBucket() {
        return this.bucket;
    }

    public ExasolObjectFactory getExasolObjectFactory() {
        return this.exasolObjectFactory;
    }

    public VirtualSchema.Builder getPreconfiguredVirtualSchemaBuilder(final String schemaName) {
        return this.exasolObjectFactory.createVirtualSchemaBuilder(schemaName)
                .connectionDefinition(this.connectionDefinition).adapterScript(this.adapterScript);
    }

}
