package com.exasol.adapter.dialects.bigquery;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.junit.jupiter.api.*;

import com.exasol.adapter.dialects.bigquery.util.BucketFsFolder;
import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;
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
            .withLocalFolder("target");

    @BeforeAll
    static void beforeAll() throws Exception {
        setupExasolContainer();
    }

    private static void setupExasolContainer() throws Exception {
        final ZipDownloader monolithic = ZipDownloader.monolithic( //
                JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalCopy());
        final ZipDownloader extracting = ZipDownloader.extracting( //
                JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalCopy());

        if (!extracting.localCopyExists()) {
            extracting.download();
        }

        if (!monolithic.localCopyExists()) {
            monolithic.download();
        }

        final BucketFsFolder bucketFs = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        // ensure there is no file with name we want to use for folder
        bucketFs.deleteFile();
        new BucketFsFolder(EXASOL.getDefaultBucket(), "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip")
                .deleteFile();

//        EXASOL.getDefaultBucket().uploadFile(monolithic.getLocalCopy(), monolithic.getFilename());
        EXASOL.getDefaultBucket().uploadFile(monolithic.getLocalCopy(), "extracted/" + monolithic.getFilename());

        for (final Path file : extracting.inventory("*.jar")) {
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
        BucketFsFolder inventory = new BucketFsFolder(EXASOL.getDefaultBucket(), JDBC_DRIVER.getBucketFsFolder());
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));
        inventory = new BucketFsFolder(EXASOL.getDefaultBucket(), "extracted");
        inventory.fullPaths().forEach(f -> System.out.println("- " + f));
    }
}
