package com.exasol.adapter.dialects.bigquery;

import com.exasol.adapter.AdapterProperties;
import com.exasol.adapter.dialects.SqlDialect;
import com.exasol.adapter.dialects.SqlDialectFactory;
import com.exasol.adapter.jdbc.ConnectionFactory;
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
    public SqlDialect createSqlDialect(final ConnectionFactory connectionFactory, final AdapterProperties properties) {
        return new BigQuerySqlDialect(connectionFactory, properties);
    }

    @Override
    public String getSqlDialectVersion() {
        final VersionCollector versionCollector = new VersionCollector(
                "META-INF/maven/com.exasol/bigquery-virtual-schema/pom.properties");
        return versionCollector.getVersionNumber();
    }
}