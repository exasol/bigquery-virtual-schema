package com.exasol.adapter.dialects.bigquery;

import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;

@Tag("integration")
class BigQueryVirtualSchemaIT {
    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final ExasolTestSetup EXASOL = new ExasolTestSetupFactory(Paths.get("dummy.json")).getTestSetup();

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
    void test() {

    }
}
