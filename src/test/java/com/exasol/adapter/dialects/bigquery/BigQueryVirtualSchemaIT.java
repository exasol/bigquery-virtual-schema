package com.exasol.adapter.dialects.bigquery;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.exasol.adapter.dialects.bigquery.testcontainer.BigQueryEmulatorContainer;
import com.exasol.adapter.dialects.bigquery.util.BucketFsFolder;
import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;
import com.exasol.bucketfs.BucketAccessException;
import com.exasol.exasoltestsetup.ExasolTestSetup;
import com.exasol.exasoltestsetup.ExasolTestSetupFactory;

@Tag("integration")
@Testcontainers
class BigQueryVirtualSchemaIT {

    private static final Logger LOGGER = Logger.getLogger(BigQueryVirtualSchemaIT.class.getName());
    private static final ExasolTestSetup EXASOL = new ExasolTestSetupFactory(Paths.get("dummy.json")).getTestSetup();
    static final JdbcDriver JDBC_DRIVER = new JdbcDriver() //
            .withSourceUrl("https://storage.googleapis.com/simba-bq-release/jdbc/" //
                    + "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip") //
            .withLocalFolder("target");

    @Container
    static BigQueryEmulatorContainer bigQuery = new BigQueryEmulatorContainer();

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

        final BucketFsFolder bucketFs = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        // ensure there is no file with name we want to use for folder
        bucketFs.deleteFile();

        for (final Path file : downloader.inventory("*.jar")) {
            final String target = JDBC_DRIVER.getPathInBucketFs(file);
            if (bucketFs.contains(file)) {
                LOGGER.fine("File already available in bucketfs: " + target);
            } else {
                LOGGER.fine("Uploading to bucketfs: " + target);
                EXASOL.getDefaultBucket().uploadFile(file, target);
            }
        }
    }

    @Test
    void test() throws BucketAccessException {
        final BucketFsFolder inventory = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));
    }
}
