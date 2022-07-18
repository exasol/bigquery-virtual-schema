package com.exasol.adapter.dialects.bigquery;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import com.exasol.adapter.dialects.bigquery.zip.*;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;

@Tag("integration")
class BigQueryVirtualSchemaIT {

    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final ExasolTestSetup EXASOL = new ExasolTestSetupFactory(Paths.get("dummy.json")).getTestSetup();
    static final JdbcDriver JDBC_DRIVER = new JdbcDriver() //
            .withSourceUrl("https://storage.googleapis.com/simba-bq-release/jdbc/" //
                    + "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip") //
            .withLocalFolder("target") //
            .withExasolBucketFsFolder("bigquery-jdbc-driver");

    @BeforeAll
    static void beforeAll() throws Exception {
        setupExasolContainer();
    }

    private static void setupExasolContainer() throws Exception {
        final ZipDownloader downloader = new ZipDownloader( //
                JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalCopy());

        if (!downloader.localFolderExists()) {
            downloader.extractToLocalFolder();
        }

        // won't delete a folder, at least not if folder is not empty
        EXASOL.getDefaultBucket().deleteFileNonBlocking(JDBC_DRIVER.getBucketFsFolder());

        // 1. Ask Sebastian: Shouldn't purge DB clean bucket fs, too?
        // see com.exasol.containers.ExasolContainer#purgeDatabase()
        // INFO: Purging database for a clean setup

        final BucketFsInventory bucketFs = new BucketFsInventory(EXASOL.getDefaultBucket(),
                JDBC_DRIVER.getBucketFsFolder());
        final List<Path> files = downloader.inventory("*.jar");

        for (final Path path : files) {
            final String target = JDBC_DRIVER.getPathInBucketFs(path);
            if (bucketFs.contains(path)) {
                LOGGER.fine("File already available in bucketfs: " + target);
            } else {
                LOGGER.fine("Uploading to bucketfs: " + target);
                EXASOL.getDefaultBucket().uploadFile(path, target);
            }
        }
    }

    @Test
    void test() throws BucketAccessException {
        final BucketFsInventory inventory = new BucketFsInventory(EXASOL.getDefaultBucket(),
                JDBC_DRIVER.getBucketFsFolder());
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));
    }
}
