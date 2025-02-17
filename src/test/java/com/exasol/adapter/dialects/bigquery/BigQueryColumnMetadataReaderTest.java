package com.exasol.adapter.dialects.bigquery;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Types;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.BaseIdentifierConverter;
import com.exasol.adapter.jdbc.JDBCTypeDescription;
import com.exasol.adapter.metadata.DataType;
import com.exasol.adapter.metadata.DataType.ExaCharset;

class BigQueryColumnMetadataReaderTest {
    private BigQueryColumnMetadataReader columnMetadataReader;
    private final static int VALUE_IGNORED = Integer.MAX_VALUE;

    @BeforeEach
    void beforeEach() {
        this.columnMetadataReader = new BigQueryColumnMetadataReader(null, AdapterProperties.emptyProperties(),
                BaseIdentifierConverter.createDefault());
    }

    @Test
    void mapDecimalReturnDecimal() {
        final JDBCTypeDescription typeDescription = new JDBCTypeDescription(Types.TIME, 0, 0, 10, "TIME");
        assertThat(this.columnMetadataReader.mapJdbcType(typeDescription),
                equalTo(DataType.createVarChar(30, DataType.ExaCharset.UTF8)));
    }

    static Stream<Arguments> types() {
        return Stream.of(
                // Overrides for BigQuery
                typeTest(new JDBCTypeDescription(Types.TIME, VALUE_IGNORED, VALUE_IGNORED, VALUE_IGNORED, "ignored"),
                        DataType.createVarChar(30, DataType.ExaCharset.UTF8)),
                typeTest(new JDBCTypeDescription(Types.NUMERIC, VALUE_IGNORED, VALUE_IGNORED, VALUE_IGNORED, "ignored"),
                        DataType.createDouble()),
                typeTest(new JDBCTypeDescription(VALUE_IGNORED, VALUE_IGNORED, VALUE_IGNORED, VALUE_IGNORED,
                        "GEOGRAPHY"), DataType.createGeometry(0)),

                // Sample tests for base class
                typeTest(new JDBCTypeDescription(Types.VARCHAR, 0, 10, 0, "ignored"),
                        DataType.createVarChar(10, ExaCharset.UTF8)),
                typeTest(new JDBCTypeDescription(Types.DECIMAL, 3, 5, 0, "ignored"), DataType.createDecimal(5, 3)));
    }

    private static Arguments typeTest(final JDBCTypeDescription bigQueryType, final DataType exasolType) {
        return Arguments.of(bigQueryType, exasolType);
    }

    @ParameterizedTest
    @MethodSource("types")
    void mapTypes(final JDBCTypeDescription bigQueryType, final DataType exasolType) {
        assertThat(this.columnMetadataReader.mapJdbcType(bigQueryType), equalTo(exasolType));
    }
}
