package com.exasol.adapter.dialects.bigquery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;

import org.junit.jupiter.api.Test;

import com.exasol.adapter.dialects.bigquery.util.JdbcDriver;
import com.exasol.adapter.dialects.bigquery.util.zip.ZipDownloader;

public class ZipDownloaderTest {

    private static final String FILENAME = "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029";
    private static final String EXTENSION = ".zip";
    private static final String PATH = "target/a/";

    static final JdbcDriver JDBC_DRIVER = new JdbcDriver() //
            .withSourceUrl("https://storage.googleapis.com/simba-bq-release/jdbc/" //
                    + FILENAME + EXTENSION) //
            .withLocalFolder("target/a");

    @Test
    void monolythic() throws IOException, URISyntaxException {
        verify(ZipDownloader::monolithic, ".zip");
    }

    @Test
    void extracting() throws IOException, URISyntaxException {
        verify(ZipDownloader::extracting, "");
    }

    private void verify(final TesteeCreator creator, final String extension) throws IOException, URISyntaxException {
        final ZipDownloader testee = creator.create(JDBC_DRIVER.getDownloadUrl(), JDBC_DRIVER.getLocalFolder());
        Files.createDirectories(Paths.get(PATH));
        assertThat(testee.getFilename(), equalTo(FILENAME + extension));
        assertThat(testee.getLocalCopy(), equalTo(Paths.get(PATH + FILENAME + extension)));
        if (Files.exists(testee.getLocalCopy())) {
            Files.delete(testee.getLocalCopy());
        }
        assertThat(testee.localCopyExists(), is(false));
        testee.download();
        assertThat(testee.localCopyExists(), is(true));
    }

    @FunctionalInterface
    interface TesteeCreator {
        ZipDownloader create(String url, Path localFolder);
    }

}
