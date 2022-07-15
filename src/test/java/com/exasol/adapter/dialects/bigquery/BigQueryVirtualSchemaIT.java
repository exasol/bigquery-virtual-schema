package com.exasol.adapter.dialects.bigquery;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.dialects.bigquery.testcontainer.BigQueryEmulatorContainer;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;

@Tag("integration")
@Testcontainers
class BigQueryVirtualSchemaIT {
    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final ExasolTestSetup EXASOL = new ExasolTestSetupFactory(Paths.get("dummy.json")).getTestSetup();

    @Container
    static BigQueryEmulatorContainer bigQuery = new BigQueryEmulatorContainer();

    @BeforeAll
    static void beforeAll() throws FileNotFoundException, BucketAccessException, TimeoutException {
        setupExasolContainer();
    }

    private static List<String> setupExasolContainer()
            throws FileNotFoundException, BucketAccessException, TimeoutException {
        EXASOL.getDefaultBucket().uploadFile(Paths.get(""), "bigquery-jdbc-driver");
        return List.of();
    }

    @Test
    void test() throws SQLException {
        final Connection connection = bigQuery.createConnection("");
        connection.createStatement().executeQuery("select 1");
        connection.close();
    }
}
