package com.exasol.adapter.dialects.bigquery;

import java.math.BigInteger;
import java.sql.*;
import java.sql.Date;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.metadata.DataType;

/**
 * Builds a {@code SELECT * FROM VALUES} query from the rows of a {@link ResultSet}.
 */
class ValueQueryBuilder {
    private static final Logger LOGGER = Logger.getLogger(ValueQueryBuilder.class.getName());
    private static final ZoneId UTC_TIMEZONE_ID = ZoneId.of("UTC");
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(UTC_TIMEZONE_ID);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(UTC_TIMEZONE_ID);

    private final Calendar utcCalendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE_ID));
    private final StringBuilder builder = new StringBuilder();
    private final SqlDialect dialect;
    private final List<DataType> selectListDataTypes;
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;

    ValueQueryBuilder(final SqlDialect dialect, final List<DataType> selectListDataTypes, final ResultSet resultSet)
            throws SQLException {
        this.dialect = dialect;
        this.selectListDataTypes = selectListDataTypes;
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
        for (int col = 1; col <= columnCount; ++col) {
            if (col > 1) {
                builder.append(", ");
            }
            appendColumnValue(rowNumber, col);
        }
        builder.append(")");
    }

    private void appendColumnValue(final int rowNumber, final int columnNumber) throws SQLException {
        final String columnName = metaData.getColumnName(columnNumber);
        final JDBCType type = JDBCType.valueOf(metaData.getColumnType(columnNumber));
        final String typeName = metaData.getColumnTypeName(columnNumber);
        final DataType expectedExasolType = selectListDataTypes.get(columnNumber - 1);
        LOGGER.info(() -> "Mapping row " + rowNumber + " / column '" + columnName + "' (" + columnNumber + ") of type "
                + type + " (" + type.getVendorTypeNumber() + ") / type name '" + typeName + "', expected type "
                + expectedExasolType);

        System.out.println("Mapping row " + rowNumber + " / column '" + columnName + "' (" + columnNumber + ") of type "
                + type + " (" + type.getVendorTypeNumber() + ") / type name '" + typeName + "', expected type "
                + expectedExasolType);

        if ("GEOGRAPHY".equals(typeName)) {
            appendGeometry(columnName, expectedExasolType);
            return;
        }

        switch (type) {
        case BIGINT:
        case INTEGER:
            appendBigInt(columnName, expectedExasolType);
            break;
        case DECIMAL:
        case NUMERIC:
        case DOUBLE:
            appendDouble(columnName, expectedExasolType);
            break;
        case BOOLEAN:
            appendBoolean(columnName, expectedExasolType);
            break;
        case DATE:
            appendDate(columnName, expectedExasolType);
            break;
        case TIMESTAMP:
            appendTimestamp(columnName, expectedExasolType);
            break;
        case VARCHAR:
        case CHAR:
            appendVarchar(columnName, expectedExasolType);
            break;
        case TIME:
            appendVarchar(columnName, expectedExasolType);
            break;
        case VARBINARY:
        default:
            LOGGER.info(
                    () -> "Mapping unknown column " + columnName + " of type " + type + "/" + typeName + " to string");
            appendString(columnName, expectedExasolType);
            break;
        }
    }

    private void appendVarchar(final String columnName, final DataType dataType) throws SQLException {
        appendString(columnName, dataType);
    }

    private void appendGeometry(final String columnName, final DataType dataType) throws SQLException {
        final String value = resultSet.getString(columnName);
        final String literal = resultSet.wasNull() ? "NULL" : "'" + value + "'";
        builder.append(castType(literal, dataType));
    }

    private void appendBigInt(final String columnName, final DataType dataType) throws SQLException {
        final String string = resultSet.getString(columnName);
        final String literal = resultSet.wasNull() ? "NULL" : new BigInteger(string).toString(10);
        builder.append(castType(literal, dataType));
    }

    private void appendDouble(final String columnName, final DataType dataType) throws SQLException {
        final double value = resultSet.getDouble(columnName);
        final String literal = resultSet.wasNull() ? "NULL" : String.valueOf(value);
        builder.append(castType(literal, dataType));
    }

    private void appendBoolean(final String columnName, final DataType dataType) throws SQLException {
        final boolean value = resultSet.getBoolean(columnName);
        final String literal = resultSet.wasNull() ? "NULL" : String.valueOf(value);
        builder.append(castType(literal, dataType));
    }

    private void appendDate(final String columnName, final DataType dataType) throws SQLException {
        final Date date = resultSet.getDate(columnName, this.utcCalendar);
        final String literal = date == null ? "NULL" : String.format("'%s'", DATE_FORMATTER.format(date.toLocalDate()));
        builder.append(castType(literal, dataType));
    }

    private void appendTimestamp(final String columnName, final DataType dataType) throws SQLException {
        final Timestamp timestamp = resultSet.getTimestamp(columnName, this.utcCalendar);
        final String literal = timestamp == null ? "NULL"
                : String.format("'%s'", TIMESTAMP_FORMATTER.format(timestamp.toInstant()));
        builder.append(castType(literal, dataType));
    }

    private String castType(final String literal, final DataType type) {
        return String.format("CAST(%s as %s)", literal, type.toString());
    }

    private void appendString(final String columnName, final DataType dataType) throws SQLException {
        final String value = resultSet.getString(columnName);
        final String literal = value == null ? "NULL" : this.dialect.getStringLiteral(value);
        builder.append(castType(literal, dataType));
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
            final DataType type = selectListDataTypes.get(i);
            joiner.add(castType(getDummyLiteral(type), type));
        }
        builder.append(joiner.toString());
        builder.append(") WHERE false");
    }

    private String getDummyLiteral(final DataType type) {
        switch (type.getExaDataType()) {
        case DATE:
            return "'0001-01-01'";
        case TIMESTAMP:
            return "'0001-01-01 00:00:00'";
        case GEOMETRY:
            return "'POINT(0 0)'";
        default:
            return "1";
        }
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
