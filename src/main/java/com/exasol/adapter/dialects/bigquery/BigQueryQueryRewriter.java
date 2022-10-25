package com.exasol.adapter.dialects.bigquery;

import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterException;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlGenerator;
import com.exasol.adapter.dialects.rewriting.ImportIntoTemporaryTableQueryRewriter;
import com.exasol.adapter.dialects.rewriting.SqlGenerationContext;
import com.exasol.adapter.jdbc.ConnectionFactory;
import com.exasol.adapter.jdbc.RemoteMetadataReader;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.sql.SqlStatement;

/**
 * This class implements a BigQuery-specific query rewriter.
 */
public class BigQueryQueryRewriter extends ImportIntoTemporaryTableQueryRewriter {
    private static final Logger LOGGER = Logger.getLogger(BigQueryQueryRewriter.class.getName());
    private static final String CAST = "CAST";
    private static final String CAST_NULL_AS_VARCHAR_4 = CAST + " (NULL AS VARCHAR(4))";

    private static final ZoneId UTC_TIMEZONE_ID = ZoneId.of("UTC");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(UTC_TIMEZONE_ID);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(UTC_TIMEZONE_ID);
    private final Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE_ID));

    /**
     * Create a new instance of the {@link BigQueryQueryRewriter}.
     *
     * @param dialect              Big Query dialect
     * @param remoteMetadataReader remote metadata reader
     * @param connectionFactory    factory for the JDBC connection to remote data source
     */
    public BigQueryQueryRewriter(final SqlDialect dialect, final RemoteMetadataReader remoteMetadataReader,
            final ConnectionFactory connectionFactory) {
        super(dialect, remoteMetadataReader, connectionFactory);
    }

    @Override
    public String rewrite(final SqlStatement statement, final List<DataType> selectListDataTypes,
            final ExaMetadata exaMetadata, final AdapterProperties properties) throws AdapterException, SQLException {
        final String query = getQueryFromStatement(statement, properties);
        LOGGER.fine(() -> "Query to rewrite: " + query);
        final StringBuilder builder = new StringBuilder();
        try (final ResultSet resultSet = this.connectionFactory.getConnection().createStatement().executeQuery(query)) {
            builder.append("SELECT * FROM VALUES");
            int rowNumber = 0;
            final ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                if (rowNumber > 0) {
                    builder.append(",");
                }
                appendRow(builder, resultSet, metaData);
                ++rowNumber;
            }
            if (rowNumber == 0) {
                appendQueryForEmptyTable(builder, metaData);
            }
        }
        final String rewrittenQuery = builder.toString();
        LOGGER.fine(() -> "Rewritten query: " + rewrittenQuery);
        return rewrittenQuery;
    }

    private String getQueryFromStatement(final SqlStatement statement, final AdapterProperties properties)
            throws AdapterException {
        final SqlGenerationContext context = new SqlGenerationContext(properties.getCatalogName(),
                properties.getSchemaName(), false);
        final SqlGenerator sqlGeneratorVisitor = this.dialect.getSqlGenerator(context);
        return sqlGeneratorVisitor.generateSqlFor(statement);
    }

    private void appendQueryForEmptyTable(final StringBuilder builder, final ResultSetMetaData metaData)
            throws SQLException {
        final int columnCounter = metaData.getColumnCount();
        builder.append("(");
        final StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < columnCounter; i++) {
            joiner.add("1");
        }
        builder.append(joiner.toString());
        builder.append(") WHERE false");
    }

    private void appendRow(final StringBuilder builder, final ResultSet resultSet, final ResultSetMetaData metadata)
            throws SQLException {
        final int columnCount = metadata.getColumnCount();
        builder.append(" (");
        for (int i = 1; i <= columnCount; ++i) {
            final String columnName = metadata.getColumnName(i);
            if (i > 1) {
                builder.append(", ");
            }
            appendColumnValue(builder, resultSet, columnName, metadata.getColumnType(i), metadata.getColumnTypeName(i));
        }
        builder.append(")");
    }

    private void appendColumnValue(final StringBuilder builder, final ResultSet resultSet, final String columnName,
            final int type, final String typeName) throws SQLException {
        LOGGER.fine(() -> "Mapping column " + columnName + " of type " + type + "/" + typeName);

        if ("GEOGRAPHY".equals(typeName)) {
            appendGeometry(builder, resultSet, columnName);
            return;
        }

        switch (type) {
        case Types.BIGINT:
        case Types.INTEGER:
            appendBigInt(builder, resultSet, columnName);
            break;
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
            appendDouble(builder, resultSet, columnName);
            break;
        case Types.BOOLEAN:
            appendBoolean(builder, resultSet, columnName);
            break;
        case Types.DATE:
            appendDate(builder, resultSet, columnName);
            break;
        case Types.TIMESTAMP:
            appendTimestamp(builder, resultSet, columnName);
            break;
        case Types.VARCHAR:
        case Types.CHAR:
            appendVarchar(builder, resultSet, columnName);
            break;
        case Types.TIME:
            appendVarchar(builder, resultSet, columnName);
            break;
        case Types.VARBINARY:
        default:
            LOGGER.info(
                    () -> "Mapping unknown column " + columnName + " of type " + type + "/" + typeName + " to string");
            appendString(builder, resultSet, columnName);
            break;
        }
    }

    private void appendVarchar(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final String stringLiteral = this.dialect.getStringLiteral(resultSet.getString(columnName));
        builder.append(resultSet.wasNull() ? CAST_NULL_AS_VARCHAR_4 : stringLiteral);
    }

    private void appendGeometry(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        String value = resultSet.getString(columnName);
        value = resultSet.wasNull() ? "NULL" : "'" + value + "'";
        builder.append(CAST + " (" + value + " AS GEOMETRY)");
    }

    private void appendBigInt(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final String string = resultSet.getString(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS DECIMAL(19,0))" : new BigInteger(string));
    }

    private void appendDouble(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final double value = resultSet.getDouble(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS DOUBLE)" : value);
    }

    private void appendBoolean(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final boolean value = resultSet.getBoolean(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS BOOLEAN)" : value);
    }

    private void appendDate(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final Date date = resultSet.getDate(columnName, this.utcCalendar);
        if (date == null) {
            builder.append(CAST + " (NULL AS DATE)");
        } else {
            builder.append(CAST + "('" + DATE_FORMATTER.format(date.toLocalDate()) + "' AS DATE)");
        }
    }

    private void appendTimestamp(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(columnName, this.utcCalendar);
        if (timestamp == null) {
            builder.append(CAST + " (NULL AS TIMESTAMP)");
        } else {
            builder.append("CAST ('");
            builder.append(TIMESTAMP_FORMATTER.format(timestamp.toInstant()));
            builder.append("' AS TIMESTAMP)");
        }
    }

    private void appendString(final StringBuilder builder, final ResultSet resultSet, final String columnName)
            throws SQLException {
        final String value = resultSet.getString(columnName);
        if (value == null) {
            builder.append(CAST_NULL_AS_VARCHAR_4);
        } else {
            builder.append(this.dialect.getStringLiteral(value));
        }
    }
}