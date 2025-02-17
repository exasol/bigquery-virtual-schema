package com.exasol.adapter.dialects.bigquery;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.*;

import com.exasol.adapter.dialects.bigquery.util.BigQueryDatasetFixture.BigQueryTable;
import com.exasol.adapter.dialects.bigquery.util.IntegrationTestSetup;
import com.exasol.adapter.dialects.bigquery.util.TestConfig;
import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;
import com.exasol.matcher.ResultSetStructureMatcher;
import com.google.cloud.bigquery.*;

@Tag("integration")
class BigQueryVirtualSchemaIT {
    private static final TestConfig CONFIG = TestConfig.read();
    private static IntegrationTestSetup setup;

    @BeforeAll
    static void beforeAll() {
        assumeTrue(CONFIG.hasGoogleCloudCredentials(), "Local bigquery emulator not yet supported");
        setup = IntegrationTestSetup.create(CONFIG);
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (setup != null) {
            setup.close();
        }
    }

    @AfterEach
    void after() {
        setup.dropCreatedObjects();
    }

    @Test
    void emptyResultMapsColumnTypeToSmallInt() throws SQLException {
        final BigQueryTable table = setup.bigQueryDataset().createTable(
                Schema.of(Field.of("id", StandardSQLTypeName.INT64), Field.of("name", StandardSQLTypeName.STRING)));
        final VirtualSchema virtualSchema = setup.createVirtualSchema("virtualSchema");
        final ResultSet result = setup.getStatement()
                .executeQuery("SELECT * FROM " + table.getQualifiedName(virtualSchema));
        assertThat(result, ResultSetStructureMatcher.table("DECIMAL", "VARCHAR").matches());
    }

    List<DataTypeTestCase> createDataTypes() {
        return List.of(DataTypeTestCase.of(StandardSQLTypeName.STRING, "val", "VARCHAR", "val"),
                DataTypeTestCase.of(StandardSQLTypeName.NUMERIC, 123.456, "DOUBLE PRECISION", 123.456D),
                DataTypeTestCase.of(StandardSQLTypeName.INT64, 123456, "DECIMAL", BigDecimal.valueOf(123456)),
                DataTypeTestCase.of(StandardSQLTypeName.BIGNUMERIC, 423450983425L, "DOUBLE PRECISION",
                        4.23450983425E11D),
                DataTypeTestCase.of(StandardSQLTypeName.BOOL, true, "BOOLEAN", true),
                DataTypeTestCase.of(StandardSQLTypeName.DATE, "2022-07-25", "DATE", date("2022-07-25")),
                DataTypeTestCase.of(StandardSQLTypeName.DATETIME, "2022-03-15 15:40:30.123", "TIMESTAMP",
                        timestamp("2022-03-15T15:40:30.123Z")),
                DataTypeTestCase.of(StandardSQLTypeName.TIMESTAMP, "2022-03-15 15:40:30.123", "TIMESTAMP",
                        timestamp("2022-03-15T15:40:30.123Z")),
                DataTypeTestCase.of(StandardSQLTypeName.FLOAT64, 3.14, "DOUBLE PRECISION", 3.14D),
                DataTypeTestCase.of(StandardSQLTypeName.GEOGRAPHY, "POINT(1 4)", "GEOMETRY", "POINT (1 4)"),
                DataTypeTestCase.of(StandardSQLTypeName.TIME, "14:15:16.123", "VARCHAR", "14:15:16.123"));
    }

    private static Date date(final String date) {
        return Date.from(LocalDate.parse(date).atStartOfDay(ZoneId.of("UTC")).toInstant());
    }

    private static Timestamp timestamp(final String timestamp) {
        return new Timestamp(Instant.parse(timestamp).toEpochMilli());
    }

    @TestFactory
    Stream<DynamicNode> dataTypeConversion() {
        final List<DataTypeTestCase> tests = createDataTypes();
        final BigQueryTable table = prepareTable(tests);
        final VirtualSchema virtualSchema = setup.createVirtualSchema("virtualSchema");
        return tests.stream().map(test -> dynamicContainer(test.getTestName(),
                Stream.of(dynamicTest("Result value " + test.expectedExasolValue, () -> {
                    try (final ResultSet result = setup.getStatement().executeQuery("SELECT \"" + test.getColumnName()
                            + "\" FROM " + table.getQualifiedName(virtualSchema) + " ORDER BY \"id\" ASC")) {
                        assertThat(result,
                                ResultSetStructureMatcher.table(test.expectedExasolType)
                                        .withCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                                        .row(test.expectedExasolValue).row((Object) null).matches());
                    }
                }), dynamicTest("Empty result of type " + test.expectedExasolType, () -> {
                    try (final ResultSet result = setup.getStatement().executeQuery("SELECT \"" + test.getColumnName()
                            + "\" FROM " + table.getQualifiedName(virtualSchema) + " WHERE 1=2")) {
                        assertThat(result, ResultSetStructureMatcher.table(test.expectedExasolType).matches());
                    }
                }))));
    }

    private BigQueryTable prepareTable(final List<DataTypeTestCase> tests) {
        final List<Field> fields = new ArrayList<>();
        fields.add(Field.of("id", StandardSQLTypeName.INT64));
        fields.addAll(tests.stream().map(DataTypeTestCase::getField).collect(toList()));
        final BigQueryTable table = setup.bigQueryDataset().createTable(Schema.of(fields));
        insertTestData(tests, table);
        return table;
    }

    private void insertTestData(final List<DataTypeTestCase> tests, final BigQueryTable table) {
        final Map<String, Object> rowWithData = tests.stream()
                .collect(toMap(DataTypeTestCase::getColumnName, DataTypeTestCase::getBigQueryValue));
        rowWithData.put("id", 1);
        final Map<String, Object> rowWithNulls = Map.of("id", 2);
        table.insertRows(List.of(rowWithData, rowWithNulls));
    }

    static class DataTypeTestCase {
        final Field field;
        final StandardSQLTypeName bigQueryType;
        final Object bigQueryValue;
        final String expectedExasolType;
        final Object expectedExasolValue;

        private DataTypeTestCase(final StandardSQLTypeName bigQueryType, final Object bigQueryValue,
                final String expectedExasolType, final Object expectedExasolValue) {
            this.bigQueryType = Objects.requireNonNull(bigQueryType);
            this.bigQueryValue = Objects.requireNonNull(bigQueryValue);
            this.expectedExasolType = expectedExasolType;
            this.expectedExasolValue = expectedExasolValue;
            this.field = Field.of("col_" + bigQueryType, bigQueryType);
        }

        static DataTypeTestCase of(final StandardSQLTypeName bigQueryType, final Object bigQueryValue,
                final String expectedExasolType, final Object expectedExasolValue) {
            return new DataTypeTestCase(bigQueryType, bigQueryValue, expectedExasolType, expectedExasolValue);
        }

        public String getTestName() {
            return "Type " + bigQueryType + " mapped to " + expectedExasolType;
        }

        public Field getField() {
            return this.field;
        }

        String getColumnName() {
            return this.field.getName();
        }

        public Object getBigQueryValue() {
            return this.bigQueryValue;
        }
    }
}
