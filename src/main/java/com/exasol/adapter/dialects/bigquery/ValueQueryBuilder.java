package com.exasol.adapter.dialects.bigquery;

import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.adapter.dialects.SqlDialect;

/**
 * Builds a {@code SELECT * FROM VALUES} query from the rows of a {@link ResultSet}.
 */
class ValueQueryBuilder {
    private static final Logger LOGGER = Logger.getLogger(ValueQueryBuilder.class.getName());
    private static final String CAST = "CAST";
    private static final String CAST_NULL_AS_VARCHAR_4 = CAST + " (NULL AS VARCHAR(4))";
    private static final ZoneId UTC_TIMEZONE_ID = ZoneId.of("UTC");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(UTC_TIMEZONE_ID);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(UTC_TIMEZONE_ID);

    private final Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
    private final StringBuilder builder = new StringBuilder();
    private final SqlDialect dialect;
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;

    ValueQueryBuilder(final SqlDialect dialect, final ResultSet resultSet) throws SQLException {
        this.dialect = dialect;
        this.resultSet = resultSet;
        this.metaData = resultSet.getMetaData();
        builder.append("SELECT * FROM VALUES");
    }

    /**
     * Append the next row from the {@link ResultSet}. This assumes that the result set points to the next row.
     * 
     * @param rowNumber current row number (zero-based)
     * @throws SQLException
     */
    void appendRow(final int rowNumber) throws SQLException {
        if (rowNumber > 0) {
            builder.append(",");
        }
        final int columnCount = metaData.getColumnCount();
        builder.append(" (");
        for (int i = 1; i <= columnCount; ++i) {
            final String columnName = metaData.getColumnName(i);
            if (i > 1) {
                builder.append(", ");
            }
            appendColumnValue(columnName, metaData.getColumnType(i), metaData.getColumnTypeName(i));
        }
        builder.append(")");
    }

    private void appendColumnValue(final String columnName, final int type, final String typeName) throws SQLException {
        LOGGER.fine(() -> "Mapping column " + columnName + " of type " + type + "/" + typeName);

        if ("GEOGRAPHY".equals(typeName)) {
            appendGeometry(columnName);
            return;
        }

        switch (type) {
        case Types.BIGINT:
        case Types.INTEGER:
            appendBigInt(columnName);
            break;
        case Types.DECIMAL:
        case Types.NUMERIC:
        case Types.DOUBLE:
            appendDouble(columnName);
            break;
        case Types.BOOLEAN:
            appendBoolean(columnName);
            break;
        case Types.DATE:
            appendDate(columnName);
            break;
        case Types.TIMESTAMP:
            appendTimestamp(columnName);
            break;
        case Types.VARCHAR:
        case Types.CHAR:
            appendVarchar(columnName);
            break;
        case Types.TIME:
            appendVarchar(columnName);
            break;
        case Types.VARBINARY:
        default:
            LOGGER.info(
                    () -> "Mapping unknown column " + columnName + " of type " + type + "/" + typeName + " to string");
            appendString(columnName);
            break;
        }
    }

    private void appendVarchar(final String columnName) throws SQLException {
        final String stringLiteral = this.dialect.getStringLiteral(resultSet.getString(columnName));
        builder.append(resultSet.wasNull() ? CAST_NULL_AS_VARCHAR_4 : stringLiteral);
    }

    private void appendGeometry(final String columnName) throws SQLException {
        String value = resultSet.getString(columnName);
        value = resultSet.wasNull() ? "NULL" : "'" + value + "'";
        builder.append(CAST + " (" + value + " AS GEOMETRY)");
    }

    private void appendBigInt(final String columnName) throws SQLException {
        final String string = resultSet.getString(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS DECIMAL(19,0))" : new BigInteger(string));
    }

    private void appendDouble(final String columnName) throws SQLException {
        final double value = resultSet.getDouble(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS DOUBLE)" : value);
    }

    private void appendBoolean(final String columnName) throws SQLException {
        final boolean value = resultSet.getBoolean(columnName);
        builder.append(resultSet.wasNull() ? CAST + " (NULL AS BOOLEAN)" : value);
    }

    private void appendDate(final String columnName) throws SQLException {
        final Date date = resultSet.getDate(columnName, this.utcCalendar);
        if (date == null) {
            builder.append(CAST + " (NULL AS DATE)");
        } else {
            builder.append(CAST + "('" + DATE_FORMATTER.format(date.toLocalDate()) + "' AS DATE)");
        }
    }

    private void appendTimestamp(final String columnName) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(columnName, this.utcCalendar);
        if (timestamp == null) {
            builder.append(CAST + " (NULL AS TIMESTAMP)");
        } else {
            builder.append("CAST ('");
            builder.append(TIMESTAMP_FORMATTER.format(timestamp.toInstant()));
            builder.append("' AS TIMESTAMP)");
        }
    }

    private void appendString(final String columnName) throws SQLException {
        final String value = resultSet.getString(columnName);
        if (value == null) {
            builder.append(CAST_NULL_AS_VARCHAR_4);
        } else {
            builder.append(this.dialect.getStringLiteral(value));
        }
    }

    /**
     * Append dummy values in case the {@link ResultSet} was empty.
     * 
     * @throws SQLException
     */
    void appendQueryForEmptyTable() throws SQLException {
        final int columnCounter = metaData.getColumnCount();
        builder.append("(");
        final StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < columnCounter; i++) {
            joiner.add("1");
        }
        builder.append(joiner.toString());
        builder.append(") WHERE false");
    }

    /**
     * Get the built query.
     * 
     * @return SQL query
     */
    String getQuery() {
        return builder.toString();
    }
}
