package com.exasol.adapter.dialects.bigquery.util;

import java.util.*;
import java.util.logging.Logger;

import com.exasol.dbbuilder.dialects.exasol.VirtualSchema;
import com.google.cloud.bigquery.*;
import com.google.cloud.bigquery.InsertAllRequest.RowToInsert;

public class BigQueryDatasetFixture implements AutoCloseable {
    private static final Logger LOGGER = Logger.getLogger(BigQueryDatasetFixture.class.getName());
    private final List<BigQueryTable> tables = new ArrayList<>();
    private final BigQuery client;
    private final DatasetId datasetId;

    private BigQueryDatasetFixture(final BigQuery client, final DatasetId datasetId) {
        this.client = client;
        this.datasetId = datasetId;
    }

    public static BigQueryDatasetFixture create(final BigQuery client, final String projectId) {
        final DatasetId datasetId = DatasetId.of(projectId, "bigqueryVirtualSchemaTest" + System.currentTimeMillis());
        client.create(DatasetInfo.newBuilder(datasetId).build());
        return new BigQueryDatasetFixture(client, datasetId);
    }

    public DatasetId getDatasetId() {
        return datasetId;
    }

    @Override
    public void close() throws Exception {
        final boolean success = client.delete(datasetId);
        if (!success) {
            throw new IllegalStateException("Failed to delete dataset " + datasetId);
        }
    }

    public void dropCreatedObjects() {
        for (final BigQueryTable table : tables) {
            table.close();
        }
        tables.clear();
    }

    public BigQueryTable createSingleColumnTable(final Field field) {
        return createTable(Schema.of(field));
    }

    public BigQueryTable createTable(final Schema schema) {
        final TableId tableId = TableId.of(datasetId.getDataset(), "table" + System.currentTimeMillis());
        final TableDefinition tableDefinition = StandardTableDefinition.of(schema);
        final TableInfo tableInfo = TableInfo.newBuilder(tableId, tableDefinition).build();
        LOGGER.fine("Creating BigQuery table " + tableId);
        client.create(tableInfo);
        final BigQueryTable table = new BigQueryTable(client, tableInfo);
        this.tables.add(table);
        return table;
    }

    public final class BigQueryTable implements AutoCloseable {
        private final BigQuery client;
        private final TableInfo tableInfo;

        public BigQueryTable(final BigQuery client, final TableInfo tableInfo) {
            this.client = client;
            this.tableInfo = tableInfo;
        }

        @Override
        public void close() {
            LOGGER.fine("Deleting BigQuery table " + tableInfo.getTableId());
            final boolean success = client.delete(tableInfo.getTableId());
            if (!success) {
                throw new IllegalStateException("Failed to delete table " + tableInfo.getTableId());
            }
        }

        public void insertRow(final Map<String, ?> content) {
            client.insertAll(InsertAllRequest.of(tableInfo, RowToInsert.of(content)));
        }

        public String getQualifiedName(final VirtualSchema virtualSchema) {
            return "\"" + virtualSchema.getName() + "\".\"" + tableInfo.getTableId().getTable() + "\"";
        }
    }
}
