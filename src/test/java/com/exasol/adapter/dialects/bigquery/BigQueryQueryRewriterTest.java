package com.exasol.adapter.dialects.bigquery;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.*;
import java.time.Instant;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.*;
import com.exasol.adapter.dialects.rewriting.AbstractQueryRewriterTestBase;
import com.exasol.adapter.jdbc.BaseRemoteMetadataReader;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.metadata.DataType.ExaCharset;
import com.exasol.adapter.sql.SqlStatement;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BigQueryQueryRewriterTest extends AbstractQueryRewriterTestBase {

    private QueryRewriter queryRewriter;
    @Mock
    private ResultSet mockResultSet;
    @Mock
    private ResultSetMetaData mockResultSetMetaData;
    @Mock
    private Statement mockStatement;
    @Mock
    private ExaMetadata exaMetadata;

    @BeforeEach
    void beforeEach(@Mock final ConnectionFactory connectionFactoryMock) throws SQLException {
        final Connection connectionMock = this.mockConnection();
        this.statement = Mockito.mock(SqlStatement.class);
        when(connectionFactoryMock.getConnection()).thenReturn(connectionMock);
        final SqlDialectFactory factory = new BigQuerySqlDialectFactory();
        final SqlDialect dialect = factory.createSqlDialect(connectionFactoryMock, AdapterProperties.emptyProperties());
        final BaseRemoteMetadataReader metadataReader = new BaseRemoteMetadataReader(connectionMock,
                AdapterProperties.emptyProperties());
        this.queryRewriter = new BigQueryQueryRewriter(dialect, metadataReader, connectionFactoryMock);
        when(connectionMock.createStatement()).thenReturn(this.mockStatement);
        when(this.mockResultSet.getMetaData()).thenReturn(this.mockResultSetMetaData);
        when(this.mockStatement.executeQuery(any())).thenReturn(this.mockResultSet);
    }

    @Test
    void testInconsistentColumnCount() throws AdapterException, SQLException {
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(3);
        final IllegalStateException exception = assertThrows(IllegalStateException.class, this::rewrite);
        assertThat(exception.getMessage(), equalTo(
                "E-VSBIGQ-1: Column count in result set (3) is different from data types in select list (0). This is an internal error that should not happen. Please report it by opening a GitHub issue."));
    }

    @Test
    void testRewriteWithJdbcConnectionEmptyTable() throws AdapterException, SQLException {
        when(this.mockResultSet.next()).thenReturn(false);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(3);
        assertThat(
                this.queryRewriter.rewrite(this.statement,
                        List.of(DataType.createVarChar(3, ExaCharset.UTF8), DataType.createBool(),
                                DataType.createDouble()), //
                        this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES(CAST (1 AS VARCHAR(3) UTF8), CAST (1 AS BOOLEAN), CAST (1 AS DOUBLE)) WHERE false"));
    }

    static Stream<Arguments> emptyTableLiterals() {
        return Stream.of(Arguments.of(DataType.createBool(), "CAST (1 AS BOOLEAN)"),
                Arguments.of(DataType.createVarChar(10, ExaCharset.UTF8), "CAST (1 AS VARCHAR(10) UTF8)"),
                Arguments.of(DataType.createDecimal(4, 6), "CAST (1 AS DECIMAL(4, 6))"),
                Arguments.of(DataType.createDouble(), "CAST (1 AS DOUBLE)"),
                Arguments.of(DataType.createGeometry(0), "CAST ('POINT(0 0)' AS GEOMETRY(0))"),
                Arguments.of(DataType.createGeometry(42), "CAST ('POINT(0 0)' AS GEOMETRY(42))"),
                Arguments.of(DataType.createDate(), "CAST ('0001-01-01' AS DATE)"),
                Arguments.of(DataType.createTimestamp(false), "CAST ('0001-01-01 00:00:00' AS TIMESTAMP)"),
                Arguments.of(DataType.createTimestamp(true),
                        "CAST ('0001-01-01 00:00:00' AS TIMESTAMP WITH LOCAL TIME ZONE)")

        );
    }

    @ParameterizedTest
    @MethodSource("emptyTableLiterals")
    void testRewriteEmptyTable(final DataType dataType, final String expectedLiteral)
            throws AdapterException, SQLException {
        when(this.mockResultSet.next()).thenReturn(false);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        assertThat(
                this.queryRewriter.rewrite(this.statement, List.of(dataType), this.exaMetadata,
                        AdapterProperties.emptyProperties()),
                equalTo(String.format("SELECT * FROM VALUES(%s) WHERE false", expectedLiteral)));
    }

    @CsvSource({ "decimal_col, DECIMAL, 105.0", //
            "numeric_col, NUMERIC, 105.0", //
            "double_col, DOUBLE, 105.0", //
            "double_col, DOUBLE, 42", //
            "double_col, DOUBLE, 99.4" //
    })
    @ParameterizedTest
    void testRewriteWithFloatingValues(final String columnName, final JDBCType type, final double columnValue)
            throws AdapterException, SQLException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn(columnName);
        when(this.mockResultSet.getDouble(columnName)).thenReturn(columnValue);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type.getVendorTypeNumber());
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(rewrite(DataType.createDouble()),
                equalTo("SELECT * FROM VALUES (CAST (" + columnValue + " AS DOUBLE))"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void testRewriteWithBoolean(final boolean value) throws AdapterException, SQLException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn("boolean");
        when(this.mockResultSet.getBoolean("boolean")).thenReturn(value);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(Types.BOOLEAN);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(rewrite(DataType.createBool()),
                equalTo(String.format("SELECT * FROM VALUES (CAST (%s AS BOOLEAN))", value)));
    }

    @CsvSource({ "string_col, VARCHAR, hello, hello", //
            "string_col, VARCHAR, i'm, i\\'m", //
            "char_col, CHAR, char value, char value", //
            "time_col, TIME, 12:10:09.000, 12:10:09.000", //
            "time_varbinary, VARBINARY, varbinary, varbinary" //
    })
    @ParameterizedTest
    void testRewriteWithStringValues(final String columnName, final JDBCType type, final String columnValue,
            final String resultValue) throws AdapterException, SQLException {
        assertQueryWithOneStringValue(columnName, type, columnValue,
                "SELECT * FROM VALUES (CAST ('" + resultValue + "' AS VARCHAR(4) UTF8))");
    }

    @CsvSource({ "1111-01-01, 1111-01-01", //
            "2019-12-3, 2019-12-03", //
            "2019-5-02, 2019-05-02" //
    })
    @ParameterizedTest
    void testRewriteWithDateValues(final java.sql.Date columnValue, final String resultValue)
            throws AdapterException, SQLException {
        final String columnName = "col";
        when(this.mockResultSet.getDate(eq(columnName), any(Calendar.class))).thenReturn(columnValue);
        assertQueryWithOneColumn(DataType.createDate(), columnName, JDBCType.DATE, null,
                "SELECT * FROM VALUES (CAST ('" + resultValue + "' AS DATE))");
    }

    @CsvSource({ "22222.2222, 22222.2222", //
            "-99999999.99999999, -9.999999999999999E7", //
            "99999999.99999999, 9.999999999999999E7", //
            "99999999.99999999, 9.999999999999999E7", //
            "11.5, 11.5", //
            "42, 42.0" })
    @ParameterizedTest
    void testRewriteWithNumeric(final Double columnValue, final String resultValue)
            throws AdapterException, SQLException {
        final String columnName = "col";
        when(this.mockResultSet.getDouble(columnName)).thenReturn(columnValue);
        assertQueryWithOneColumn(DataType.createDouble(), columnName, JDBCType.NUMERIC, null,
                "SELECT * FROM VALUES (CAST (" + resultValue + " AS DOUBLE))");
    }

    private void assertQueryWithOneColumn(final DataType dataType, final String columnName, final JDBCType type,
            final String typeName, final String query) throws SQLException, AdapterException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn(columnName);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type.getVendorTypeNumber());
        when(this.mockResultSetMetaData.getColumnTypeName(1)).thenReturn(typeName);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(rewrite(dataType), equalTo(query));
    }

    private void assertQueryWithOneStringValue(final String columnName, final JDBCType type, final String columnValue,
            final String query) throws SQLException, AdapterException {
        when(this.mockResultSet.getString(columnName)).thenReturn(columnValue);
        assertQueryWithOneColumn(DataType.createVarChar(4, ExaCharset.UTF8), columnName, type, null, query);
    }

    @Test
    void testRewriteWithBigInt() throws AdapterException, SQLException {
        assertQueryWithOneStringValue("bigint_col", JDBCType.BIGINT, "123456",
                "SELECT * FROM VALUES (CAST (123456 AS VARCHAR(4) UTF8))");
    }

    @Test
    void testRewriteWithInteger() throws AdapterException, SQLException {
        assertQueryWithOneStringValue("int_col", JDBCType.INTEGER, "123456",
                "SELECT * FROM VALUES (CAST (123456 AS VARCHAR(4) UTF8))");
    }

    @CsvSource({ "1111-01-01T12:10:09.000000Z, 1111-01-01 12:10:09.000", //
            "1111-01-01T12:10:09.000000Z, 1111-01-01 12:10:09.000", //
            "1111-01-01T12:10:09Z, 1111-01-01 12:10:09.000", //
            "1111-01-01T12:10:09.1Z, 1111-01-01 12:10:09.100", //
            "1111-01-01T12:10:09.12Z, 1111-01-01 12:10:09.120", //
            "1111-01-01T12:10:09.123Z, 1111-01-01 12:10:09.123", //
            "1111-01-01T12:10:09.1234Z, 1111-01-01 12:10:09.123", //
            "1111-01-01T12:10:09.1239Z, 1111-01-01 12:10:09.123", //
            "1111-01-01T12:10:09.12345Z, 1111-01-01 12:10:09.123", //
            "1111-01-01T12:10:09.123666Z, 1111-01-01 12:10:09.123", //
            "1111-01-01T01:02:30Z, 1111-01-01 01:02:30.000" //
    })
    @ParameterizedTest
    void testRewriteWithDatetime(final Instant valueToConvert, final String expectedValue)
            throws AdapterException, SQLException {
        when(this.mockResultSet.getTimestamp(eq("col_timestamp"), any(Calendar.class)))
                .thenReturn(new Timestamp(valueToConvert.toEpochMilli()));
        assertQueryWithOneColumn(DataType.createTimestamp(false), "col_timestamp", JDBCType.TIMESTAMP, null,
                "SELECT * FROM VALUES (CAST ('" + expectedValue + "' AS TIMESTAMP))");
    }

    @Test
    void testRewriteWithGeography() throws AdapterException, SQLException {
        when(this.mockResultSet.getString("col")).thenReturn("POINT(1 2)");
        assertQueryWithOneColumn(DataType.createGeometry(0), "col", JDBCType.VARCHAR, "GEOGRAPHY",
                "SELECT * FROM VALUES (CAST ('POINT(1 2)' AS GEOMETRY(0)))");
    }

    @Test
    void testRewriteWithNullGeography() throws AdapterException, SQLException {
        when(this.mockResultSet.getString("col")).thenReturn(null);
        when(this.mockResultSet.wasNull()).thenReturn(true);
        assertQueryWithOneColumn(DataType.createGeometry(0), "col", JDBCType.VARCHAR, "GEOGRAPHY",
                "SELECT * FROM VALUES (CAST (NULL AS GEOMETRY(0)))");
    }

    @Test
    void testRewriteWithJdbcConnectionWithThreeRows() throws AdapterException, SQLException {
        when(this.mockResultSet.next()).thenReturn(true, true, true, false);
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn("bigInt");
        when(this.mockResultSetMetaData.getColumnName(2)).thenReturn("varchar");
        when(this.mockResultSetMetaData.getColumnName(3)).thenReturn("boolean");
        when(this.mockResultSet.getString("bigInt")).thenReturn("1", "2", "3");
        when(this.mockResultSet.getString("varchar")).thenReturn("foo", "bar", "cat");
        when(this.mockResultSet.getBoolean("boolean")).thenReturn(true, false, true);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(Types.BIGINT);
        when(this.mockResultSetMetaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        when(this.mockResultSetMetaData.getColumnType(3)).thenReturn(Types.BOOLEAN);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(3);
        assertThat(
                rewrite(DataType.createDecimal(5, 0), DataType.createVarChar(3, ExaCharset.UTF8),
                        DataType.createBool()),
                equalTo("SELECT * FROM VALUES "
                        + "(CAST (1 AS DECIMAL(5, 0)), CAST ('foo' AS VARCHAR(3) UTF8), CAST (true AS BOOLEAN)), "
                        + "(CAST (2 AS DECIMAL(5, 0)), CAST ('bar' AS VARCHAR(3) UTF8), CAST (false AS BOOLEAN)), "
                        + "(CAST (3 AS DECIMAL(5, 0)), CAST ('cat' AS VARCHAR(3) UTF8), CAST (true AS BOOLEAN))"));
    }

    @CsvSource({ "VARCHAR", "TIME", "VARBINARY" })
    @ParameterizedTest(name = "Null value of type {0}")
    void testRewriteStringWithValueNull(final JDBCType type) throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(type);
        assertThat(rewrite(DataType.createVarChar(4, ExaCharset.UTF8)),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS VARCHAR(4) UTF8))"));
    }

    private void mockOneRowWithOneColumnOfType(final JDBCType type) throws SQLException {
        when(this.mockResultSet.next()).thenReturn(true, false);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type.getVendorTypeNumber());
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.wasNull()).thenReturn(true);
    }

    @Test
    void testRewriteNumericWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.NUMERIC);
        assertThat(rewrite(DataType.createDouble()), equalTo("SELECT * FROM VALUES (CAST (NULL AS DOUBLE))"));
    }

    @Test
    void testRewriteBigIntWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.BIGINT);
        assertThat(rewrite(DataType.createDecimal(19, 9)),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS DECIMAL(19, 9)))"));
    }

    @Test
    void testRewriteTimestampWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.TIMESTAMP);
        assertThat(rewrite(DataType.createTimestamp(false)),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS TIMESTAMP))"));
    }

    @Test
    void testRewriteDateWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.DATE);
        assertThat(rewrite(DataType.createDate()), equalTo("SELECT * FROM VALUES (CAST (NULL AS DATE))"));
    }

    @Test
    void testRewriteBooleanWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.BOOLEAN);
        assertThat(rewrite(DataType.createBool()), equalTo("SELECT * FROM VALUES (CAST (NULL AS BOOLEAN))"));
    }

    @Test
    void testRewriteDoubleWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(JDBCType.DOUBLE);
        assertThat(rewrite(DataType.createDouble()), equalTo("SELECT * FROM VALUES (CAST (NULL AS DOUBLE))"));
    }

    private String rewrite(final DataType... selectListDataTypes) throws AdapterException, SQLException {
        return this.queryRewriter.rewrite(this.statement, asList(selectListDataTypes), //
                this.exaMetadata, AdapterProperties.emptyProperties());
    }
}
