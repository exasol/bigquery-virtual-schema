package com.exasol.adapter.dialects.bigquery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
 * This class implements a BigQuery-specific query rewriter. It generates a {@code SELECT * FROM VALUES} query using the
 * actual values returned by the BigQuery SQL statement.
 */
public class BigQueryQueryRewriter extends ImportIntoTemporaryTableQueryRewriter {
    private static final Logger LOGGER = Logger.getLogger(BigQueryQueryRewriter.class.getName());

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
        LOGGER.fine(() -> String.format("Query to rewrite: '%s'", query));
        try (final ResultSet resultSet = this.connectionFactory.getConnection().createStatement().executeQuery(query)) {
            int rowNumber = 0;
            final ValueQueryBuilder rewriter = new ValueQueryBuilder(dialect, selectListDataTypes, resultSet);
            while (resultSet.next()) {
                rewriter.appendRow(rowNumber);
                ++rowNumber;
            }
            if (rowNumber == 0) {
                rewriter.appendQueryForEmptyTable();
            }
            final String rewrittenQuery = rewriter.getQuery();
            LOGGER.fine(() -> String.format("Rewritten query: '%s'", rewrittenQuery));
            return rewrittenQuery;
        }
    }

    private String getQueryFromStatement(final SqlStatement statement, final AdapterProperties properties)
            throws AdapterException {
        final SqlGenerationContext context = new SqlGenerationContext(properties.getCatalogName(),
                properties.getSchemaName(), false);
        final SqlGenerator sqlGeneratorVisitor = this.dialect.getSqlGenerator(context);
        return sqlGeneratorVisitor.generateSqlFor(statement);
    }
}
