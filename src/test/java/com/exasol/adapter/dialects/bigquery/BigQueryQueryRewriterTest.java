package com.exasol.adapter.dialects.bigquery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.*;
import java.time.Instant;
import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
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
    void testRewriteWithJdbcConnectionEmptyTable() throws AdapterException, SQLException {
        when(this.mockResultSet.next()).thenReturn(false);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(5);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES(1, 1, 1, 1, 1) WHERE false"));
    }

    @CsvSource({ "decimal_col, 3, 105.0", //
            "numeric_col, 2, 105.0", //
            "double_col, 8, 105.0", //
            "double_col, 8, 99.4" //
    })
    @ParameterizedTest
    void testRewriteWithFloatingValues(final String columnName, final int type, final double columnValue)
            throws AdapterException, SQLException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn(columnName);
        when(this.mockResultSet.getDouble(columnName)).thenReturn(columnValue);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (" + columnValue + ")"));
    }

    @Test
    void testRewriteWithBoolean() throws AdapterException, SQLException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn("boolean");
        when(this.mockResultSet.getBoolean("boolean")).thenReturn(true);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(Types.BOOLEAN);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (true)"));
    }

    @CsvSource({ "string_col, 12, hello, hello", //
            "string_col, 12, i'm, i\\'m", //
            "char_col, 1, char value, char value", //
            "time_col, 92, 12:10:09.000, 12:10:09.000", //
            "time_varbinary, -3, varbinary, varbinary" //
    })
    @ParameterizedTest
    void testRewriteWithStringValues(final String columnName, final int type, final String columnValue,
            final String resultValue) throws AdapterException, SQLException {
        assertQueryWithOneStringValue(columnName, type, columnValue, "SELECT * FROM VALUES ('" + resultValue + "')");
    }

    @CsvSource({ "1111-01-01, 1111-01-01", //
            "2019-12-3, 2019-12-03", //
            "2019-5-02, 2019-05-02" //
    })
    @ParameterizedTest
    void testRewriteWithDateValues(final java.sql.Date columnValue, final String resultValue)
            throws AdapterException, SQLException {
        final String columnName = "col";
        final int type = Types.DATE;
        when(this.mockResultSet.getDate(eq(columnName), any(Calendar.class))).thenReturn(columnValue);
        assertQueryWithOneColumn(columnName, type, null, "SELECT * FROM VALUES (CAST('" + resultValue + "' AS DATE))");
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
        final int type = Types.NUMERIC;
        when(this.mockResultSet.getDouble(columnName)).thenReturn(columnValue);
        assertQueryWithOneColumn(columnName, type, null, "SELECT * FROM VALUES (" + resultValue + ")");
    }

    private void assertQueryWithOneColumn(final String columnName, final int type, final String typeName,
            final String query) throws SQLException, AdapterException {
        when(this.mockResultSetMetaData.getColumnName(1)).thenReturn(columnName);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type);
        when(this.mockResultSetMetaData.getColumnTypeName(1)).thenReturn(typeName);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.next()).thenReturn(true, false);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo(query));
    }

    private void assertQueryWithOneStringValue(final String columnName, final int type, final String columnValue,
            final String query) throws SQLException, AdapterException {
        when(this.mockResultSet.getString(columnName)).thenReturn(columnValue);
        assertQueryWithOneColumn(columnName, type, null, query);
    }

    @Test
    void testRewriteWithBigInt() throws AdapterException, SQLException {
        assertQueryWithOneStringValue("bigint_col", Types.BIGINT, "123456", "SELECT * FROM VALUES (123456)");
    }

    @Test
    void testRewriteWithInteger() throws AdapterException, SQLException {
        assertQueryWithOneStringValue("int_col", Types.INTEGER, "123456", "SELECT * FROM VALUES (123456)");
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
        when(this.mockResultSet.getTimestamp(eq("timestamp"), any(Calendar.class)))
                .thenReturn(new Timestamp(valueToConvert.toEpochMilli()));
        assertQueryWithOneColumn("timestamp", Types.TIMESTAMP, null,
                "SELECT * FROM VALUES (CAST ('" + expectedValue + "' AS TIMESTAMP))");
    }

    @Test
    void testRewriteWithGeography() throws AdapterException, SQLException {
        when(this.mockResultSet.getString("col")).thenReturn("POINT(1 2)");
        assertQueryWithOneColumn("col", Types.VARCHAR, "GEOGRAPHY",
                "SELECT * FROM VALUES (CAST ('POINT(1 2)' AS GEOMETRY))");
    }

    @Test
    void testRewriteWithNullGeography() throws AdapterException, SQLException {
        when(this.mockResultSet.getString("col")).thenReturn(null);
        when(this.mockResultSet.wasNull()).thenReturn(true);
        assertQueryWithOneColumn("col", Types.VARCHAR, "GEOGRAPHY", "SELECT * FROM VALUES (CAST (NULL AS GEOMETRY))");
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
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (1, 'foo', true), (2, 'bar', false), (3, 'cat', true)"));
    }

    @ValueSource(ints = { Types.VARCHAR, Types.TIME, Types.VARBINARY })
    @ParameterizedTest(name = "Null value of type {0}")
    void testRewriteStringWithValueNull(final int type) throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(type);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS VARCHAR(4)))"));
    }

    private void mockOneRowWithOneColumnOfType(final int type) throws SQLException {
        when(this.mockResultSet.next()).thenReturn(true, false);
        when(this.mockResultSetMetaData.getColumnType(1)).thenReturn(type);
        when(this.mockResultSetMetaData.getColumnCount()).thenReturn(1);
        when(this.mockResultSet.wasNull()).thenReturn(true);
    }

    @Test
    void testRewriteNumericWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.NUMERIC);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS DOUBLE))"));
    }

    @Test
    void testRewriteBigIntWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.BIGINT);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS DECIMAL(19,0)))"));
    }

    @Test
    void testRewriteTimestampWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.TIMESTAMP);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS TIMESTAMP))"));
    }

    @Test
    void testRewriteDateWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.DATE);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS DATE))"));
    }

    @Test
    void testRewriteBooleanWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.BOOLEAN);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS BOOLEAN))"));
    }

    @Test
    void testRewriteDoubleWithValueNull() throws AdapterException, SQLException {
        mockOneRowWithOneColumnOfType(Types.DOUBLE);
        assertThat(this.queryRewriter.rewrite(this.statement, this.exaMetadata, AdapterProperties.emptyProperties()),
                equalTo("SELECT * FROM VALUES (CAST (NULL AS DOUBLE))"));
    }
}