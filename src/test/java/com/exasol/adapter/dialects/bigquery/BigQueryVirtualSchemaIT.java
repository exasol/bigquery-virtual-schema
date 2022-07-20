package com.exasol.adapter.dialects.bigquery;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.dialects.bigquery.testcontainer.BigQueryEmulatorContainer;
import com.exasol.adapter.dialects.bigquery.util.BucketFsFolder;
import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;
import com.google.cloud.bigquery.*;

@Tag("integration")
@Testcontainers
class BigQueryVirtualSchemaIT {

    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final ExasolTestSetup EXASOL = new ExasolTestSetupFactory(Paths.get("dummy.json")).getTestSetup();
    static final JdbcDriver JDBC_DRIVER = new JdbcDriver() //
            .withSourceUrl("https://storage.googleapis.com/simba-bq-release/jdbc/" //
                    + "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip") //
            .withLocalFolder("target");

    @Container
    static BigQueryEmulatorContainer bigQuery = new BigQueryEmulatorContainer(
            Paths.get("src/test/resources/bigquery-data.yaml"));

    @BeforeAll
    static void beforeAll() throws Exception {
        setupExasolContainer();
    }

    private static void setupExasolContainer() throws Exception {
        final ZipDownloader monolithic = ZipDownloader.monolithic( //
                JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalCopy());
        final ZipDownloader extracting = ZipDownloader.extracting( //
                JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalCopy());

        if (!extracting.localCopyExists()) {
            extracting.download();
        }

        if (!monolithic.localCopyExists()) {
            monolithic.download();
        }

        final BucketFsFolder bucketFs = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        // ensure there is no file with name we want to use for folder
        bucketFs.deleteFile();
        new BucketFsFolder(EXASOL.getDefaultBucket(), "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip")
                .deleteFile();

//        EXASOL.getDefaultBucket().uploadFile(monolithic.getLocalCopy(), monolithic.getFilename());
        EXASOL.getDefaultBucket().uploadFile(monolithic.getLocalCopy(), "extracted/" + monolithic.getFilename());

        for (final Path file : extracting.inventory("*.jar")) {
            final String target = JDBC_DRIVER.getPathInBucketFs(file);
            if (bucketFs.contains(file)) {
                LOGGER.fine("File already available in bucketfs: " + target);
            } else {
                LOGGER.fine("Uploading to bucketfs: " + target);
                EXASOL.getDefaultBucket().uploadFile(file, target);
            }
        }
    }

    @Test
    void test() throws BucketAccessException {
        BucketFsFolder inventory = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));
        inventory = new BucketFsFolder(EXASOL.getDefaultBucket(), "extracted");
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));

        final BigQuery client = bigQuery.getClient();
        TableResult result = client.query(QueryJobConfiguration.of("select 2 * 3"));
        result.iterateAll().forEach(row -> System.out.println("row: " + row));

        final DatasetId datasetId = DatasetId.of("mydatasetId" + System.currentTimeMillis());

        client.create(DatasetInfo.newBuilder(datasetId).build());

        final TableId tableId = TableId.of(datasetId.getDataset(), "tableName");
        final Schema schema = Schema.of(Field.of("id", StandardSQLTypeName.STRING),
                Field.of("name", StandardSQLTypeName.STRING), Field.of("status", StandardSQLTypeName.BOOL));
        final TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        final TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();

        // client.create(tableInfo);

        // client.insertAll(InsertAllRequest.of(tableId, RowToInsert.of(Map.of("id", 1, "name", "a", "status", true))));

        // result = client.query(QueryJobConfiguration.of("select * from " + tableId.toString()));
        // result = client.query(QueryJobConfiguration.of("CREATE TABLE " + datasetId.getDataset() + ".newtable ("
        // + "x INT64," + "y STRUCT<" + "a ARRAY<STRING> ," + "b BOOL" + ">)"));
        // result = client.query(QueryJobConfiguration.of("INSERT `table` (col1) VALUES (42)"));
        // result = client.query(QueryJobConfiguration.of("select * from " + datasetId.getDataset() + ".newtable"));
        result = client.query(QueryJobConfiguration.of("select * from dataset1.table_a"));
        result.iterateAll().forEach(row -> System.out.println("row: " + row));
    }
}
