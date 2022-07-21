package com.exasol.adapter.dialects.bigquery;

import static org.hamcrest.MatcherAssert.assertThat;

import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;
import com.exasol.matcher.ResultSetStructureMatcher;
import com.google.cloud.bigquery.*;

@Tag("integration")
class BigQueryVirtualSchemaIT {

    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final IntegrationTestSetup TEST_SETUP = IntegrationTestSetup
            .create(Paths.get("src/test/resources/bigquery-data.yaml"));

    @BeforeAll
    static void beforeAll() throws Exception {
    }

    @Test
    void test() throws BucketAccessException, JobException, InterruptedException {

        final BigQuery client = TEST_SETUP.getBigQueryClient();
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

    @Test
    void createVirtualSchema() throws SQLException {
        final VirtualSchema virtualSchema = TEST_SETUP.createVirtualSchema("myvs");
        final ResultSet result = TEST_SETUP.getStatement()
                .executeQuery("SELECT * from " + virtualSchema.getName() + ".table_a");
        assertThat(result, ResultSetStructureMatcher.table().row("book-1").row("book-2").matches());
    }

    @Test
    void getTables() throws SQLException {
        final ResultSet tables = TEST_SETUP.getConnection().getMetaData().getTables(null, null, null, null);
        while (tables.next()) {
            System.out.println(tables.getString(1));
        }
    }
}
