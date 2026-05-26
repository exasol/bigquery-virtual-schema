package com.exasol.adapter.dialects.bigquery;

import java.sql.Connection;
import java.sql.Types;

import com.exasol.ExaMetadata;
import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.IdentifierConverter;
import com.exasol.adapter.jdbc.BaseColumnMetadataReader;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;

/**
 * This class implements BigQuery-specific reading of column metadata.
 */
public class BigQueryColumnMetadataReader extends BaseColumnMetadataReader {
    /**
     * Create a new instance of the {@link BigQueryColumnMetadataReader}.
     *
     * @param connection          connection to the remote data source
     * @param properties          user-defined adapter properties
     * @param metadata            metadata of the Exasol database
     * @param identifierConverter converter between source and Exasol identifiers
     */
    public BigQueryColumnMetadataReader(final Connection connection, final AdapterProperties properties, final ExaMetadata metadata,
            final IdentifierConverter identifierConverter) {
        super(connection, properties, metadata, identifierConverter);
    }

    @Override
    public DataType mapJdbcType(final JDBCTypeDescription jdbcTypeDescription) {
        if (jdbcTypeDescription.getJdbcType() == Types.TIME) {
            return DataType.createVarChar(30, DataType.ExaCharset.UTF8);
        }
        if (jdbcTypeDescription.getJdbcType() == Types.NUMERIC) {
            return DataType.createDouble();
        }
        if (jdbcTypeDescription.getTypeName().equals("GEOGRAPHY")) {
            return DataType.createGeometry(0);
        }
        return super.mapJdbcType(jdbcTypeDescription);
    }
}
