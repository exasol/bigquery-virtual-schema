package com.exasol.adapter.dialects.bigquery;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
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
        System.setProperty("test.udf-logs", "true");
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

    List<DataTypeTestCase> createDataTypes() {
        return List.of(DataTypeTestCase.of(StandardSQLTypeName.STRING, "val", "VARCHAR", "val"),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.STRING, "VARCHAR"),
                DataTypeTestCase.of(StandardSQLTypeName.NUMERIC, 123.456, "DECIMAL", BigDecimal.valueOf(123.456)),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.NUMERIC, "DECIMAL"),
                DataTypeTestCase.of(StandardSQLTypeName.INT64, 123456, "INTEGER", 123456),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.INT64, "INTEGER"),
                DataTypeTestCase.of(StandardSQLTypeName.BIGNUMERIC, 423450983425L, "BIGINT", 423450983425L),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.BIGNUMERIC, "BIGINT"),
                DataTypeTestCase.of(StandardSQLTypeName.BOOL, true, "BOOLEAN", true),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.BOOL, "BOOLEAN"),
                DataTypeTestCase.of(StandardSQLTypeName.DATE, "2022-03-15", "DATE", date("2022-03-15")),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.DATE, "DATE"),
                DataTypeTestCase.of(StandardSQLTypeName.DATETIME, "2022-03-15 15:40:30.123", "TIMESTAMP",
                        timestamp("2022-03-15T15:40:30.123Z")),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.DATETIME, "TIMESTAMP"),
                DataTypeTestCase.of(StandardSQLTypeName.TIMESTAMP, "2022-03-15 15:40:30.123", "TIMESTAMP",
                        timestamp("2022-03-15T15:40:30.123Z")),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.TIMESTAMP, "TIMESTAMP"),
                DataTypeTestCase.of(StandardSQLTypeName.FLOAT64, 3.14, "DECIMAL", BigDecimal.valueOf(3.14)),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.FLOAT64, "DECIMAL"),
                DataTypeTestCase.of(StandardSQLTypeName.GEOGRAPHY, "POINT(1 4)", "GEOMETRY", "POINT (1 4)"),
                DataTypeTestCase.ofNullValue(StandardSQLTypeName.GEOGRAPHY, "GEOMETRY"));
    }

    private static Date date(final String date) {
        return Date.valueOf(LocalDate.parse(date));
    }

    private static Timestamp timestamp(final String timestamp) {
        return new Timestamp(Instant.parse(timestamp).toEpochMilli());
    }

    @TestFactory
    Stream<DynamicNode> dataTypeConversion() {
        final List<DataTypeTestCase> tests = createDataTypes();
        final List<Field> fields = tests.stream().map(DataTypeTestCase::getField).collect(toList());
        final BigQueryTable table = setup.bigQueryDataset().createTable(Schema.of(fields));
        table.insertRow(tests.stream().filter(t -> t.bigQueryValue != null)
                .collect(toMap(DataTypeTestCase::getColumnName, DataTypeTestCase::getBigQueryValue)));
        final VirtualSchema virtualSchema = setup.createVirtualSchema("myvs");

        return tests.stream().map(test -> DynamicTest.dynamicTest(test.getTestName(), () -> {
            final ResultSet result = setup.getStatement().executeQuery(
                    "SELECT \"" + test.getColumnName() + "\" from " + table.getQualifiedName(virtualSchema));
            assertThat(result,
                    ResultSetStructureMatcher.table(test.expectedExasolType)
                            .withCalendar(Calendar.getInstance(TimeZone.getTimeZone("UTC")))
                            .row(test.expectedExasolValue).matches());
        }));
    }

    static class DataTypeTestCase {
        final Field field;
        final StandardSQLTypeName bigQueryType;
        final Object bigQueryValue;
        final String expectedExasolType;
        final Object expectedExasolValue;

        private DataTypeTestCase(final StandardSQLTypeName bigQueryType, final Object bigQueryValue,
                final String expectedExasolType, final Object expectedExasolValue) {
            this.bigQueryType = bigQueryType;
            this.bigQueryValue = bigQueryValue;
            this.expectedExasolType = expectedExasolType;
            this.expectedExasolValue = expectedExasolValue;
            this.field = Field.of("col_" + bigQueryType + (bigQueryValue == null ? "_null" : ""), bigQueryType);
        }

        static DataTypeTestCase of(final StandardSQLTypeName bigQueryType, final Object bigQueryValue,
                final String expectedExasolType, final Object expectedExasolValue) {
            return new DataTypeTestCase(bigQueryType, bigQueryValue, expectedExasolType, expectedExasolValue);
        }

        static DataTypeTestCase ofNullValue(final StandardSQLTypeName bigQueryType, final String expectedExasolType) {
            return of(bigQueryType, null, expectedExasolType, null);
        }

        public String getTestName() {
            return "Type " + bigQueryType + (bigQueryValue == null ? " value null" : "") + " mapped to "
                    + expectedExasolType;
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
