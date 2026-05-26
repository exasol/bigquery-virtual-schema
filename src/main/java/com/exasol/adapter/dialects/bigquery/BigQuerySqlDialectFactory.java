package com.exasol.adapter.dialects.bigquery;

import com.exasol.adapter.dialects.*;
import com.exasol.logging.VersionCollector;

/**
 * Factory for the BigQuery SQL dialect.
 */
public class BigQuerySqlDialectFactory implements SqlDialectFactory {
    @Override
    public String getSqlDialectName() {
        return BigQuerySqlDialect.NAME;
    }

    @Override
    public SqlDialect createSqlDialect(final JDBCAdapterContext context) {
        return new BigQuerySqlDialect(context);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/bigquery-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }

    @Override
    public String getAdapterProjectShortTag() {
        return "VSBIGQ";
    }
}
