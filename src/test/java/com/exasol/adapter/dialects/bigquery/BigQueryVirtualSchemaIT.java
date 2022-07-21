package com.exasol.adapter.dialects.bigquery;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;

import com.exasol.adapter.dialects.bigquery.util.BigQueryDatasetFixture.BigQueryTable;
import com.exasol.adapter.dialects.bigquery.util.IntegrationTestSetup;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;
import com.exasol.matcher.ResultSetStructureMatcher;
import com.google.cloud.bigquery.*;

@Tag("integration")
class BigQueryVirtualSchemaIT {

    private static final IntegrationTestSetup SETUP = IntegrationTestSetup.create();

    @AfterAll
    static void afterAll() throws Exception {
        SETUP.close();
    }

    @AfterEach
    void after() {
        SETUP.dropCreatedObjects();
    }

    List<DataTypeTestCase> createDataTypes() {
        return List.of(new DataTypeTestCase(StandardSQLTypeName.STRING, "val", "VARCHAR", "val"), //
                new DataTypeTestCase(StandardSQLTypeName.BIGNUMERIC, 42, "VARCHAR", 42),
                new DataTypeTestCase(StandardSQLTypeName.BOOL, true, "BOOLEAN", true),
                new DataTypeTestCase(StandardSQLTypeName.DATE, "2022-03-15", "DATE", "2022-03-15"));
    }

    @TestFactory
    Stream<DynamicNode> dataTypeConversion() {
        final List<DataTypeTestCase> tests = createDataTypes();
        final List<Field> fields = tests.stream().map(t -> {
            t.field = Field.of("col_" + t.bigQueryType, t.bigQueryType);
            return t.field;
        }).collect(toList());
        final BigQueryTable table = SETUP.bigQueryDataset().createTable(Schema.of(fields));
        table.insertRow(
                tests.stream().collect(toMap(DataTypeTestCase::getColumnName, DataTypeTestCase::getBigQueryValue)));
        final VirtualSchema virtualSchema = SETUP.createVirtualSchema("myvs");

        return tests.stream().map(test -> DynamicTest.dynamicTest(
                "BigQuery data type " + test.bigQueryType + " mapped to " + test.expectedExasolType, () -> {
                    final ResultSet result = SETUP.getStatement().executeQuery(
                            "SELECT \"" + test.getColumnName() + "\" from " + table.getQualifiedName(virtualSchema));
                    assertThat(result, ResultSetStructureMatcher.table(test.expectedExasolType)
                            .row(test.expectedExasolValue).matches());
                }));
    }

    static class DataTypeTestCase {
        Field field;
        final StandardSQLTypeName bigQueryType;
        final Object bigQueryValue;
        final String expectedExasolType;
        final Object expectedExasolValue;

        DataTypeTestCase(final StandardSQLTypeName bigQueryType, final Object bigQueryValue,
                final String expectedExasolType, final Object expectedExasolValue) {
            this.bigQueryType = bigQueryType;
            this.bigQueryValue = bigQueryValue;
            this.expectedExasolType = expectedExasolType;
            this.expectedExasolValue = expectedExasolValue;
        }

        String getColumnName() {
            return field.getName();
        }

        public Object getBigQueryValue() {
            return bigQueryValue;
        }
    }
}
