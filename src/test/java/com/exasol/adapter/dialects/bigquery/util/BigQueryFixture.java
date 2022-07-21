package com.exasol.adapter.dialects.bigquery.util;

import com.google.cloud.bigquery.BigQuery;

public class BigQueryFixture implements AutoCloseable {

    private final BigQuery client;

    public BigQueryFixture(final BigQuery client) {
        this.client = client;
    }

    @Override
    public void close() {

    }

}
