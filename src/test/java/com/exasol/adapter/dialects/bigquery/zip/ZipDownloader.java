package com.exasol.adapter.dialects.bigquery.zip;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import lombok.Getter;

/**
 * Download zip file from URL
 */
public class ZipDownloader {

    private static final Logger LOGGER = Logger.getLogger(ZipDownloader.class.getName());

    private static final String JDBC_DOWNLOAD_URL = "https://storage.googleapis.com/simba-bq-release/jdbc/";
    private static final String FILENAME = "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip";
    private static final String TEMP_FOLDER = "target";

    final File localCopy;

    public ZipDownloader() {
        this.localCopy = new File(TEMP_FOLDER, FILENAME);
    }

    public List<String> inventory() {
        final List<String> result = new ArrayList<>();
        try (AutoClosableZipFile zf = new AutoClosableZipFile(getLocalCopy())) {
            final Enumeration<? extends ZipEntry> enumeration = zf.getZipfile().entries();
            while (enumeration.hasMoreElements()) {
                final String name = enumeration.nextElement().getName();
                if ((name != null) && name.endsWith(".jar")) {
                    LOGGER.info("adding " + name);
                    result.add(name);
                }
            }
        } catch (final IOException exception) {
            throw new ZipException(exception);
        }
        return result;
    }

    public File download() throws IOException, URISyntaxException {
        if (this.localCopy.exists()) {
            return this.localCopy;
        }
        final URL remote = new URI(JDBC_DOWNLOAD_URL).resolve(FILENAME).toURL();
        LOGGER.info("Download " + remote + " to " + this.localCopy);
        try (ReadableByteChannel input = Channels.newChannel(remote.openStream());
                FileOutputStream output = new FileOutputStream(this.localCopy)) {
            output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
        }
        return this.localCopy;
    }

    public File getLocalCopy() {
        try {
            return download();
        } catch (IOException | URISyntaxException exception) {
            throw new ZipException(exception);
        }
    }

    private static class AutoClosableZipFile implements AutoCloseable {
        @Getter
        final ZipFile zipfile;

        AutoClosableZipFile(final File file) throws IOException {
            this.zipfile = new ZipFile(file);
        }

        @Override
        public void close() {
            try {
                this.zipfile.close();
            } catch (final IOException exception) {
                throw new ZipException(exception);
            }
        }
    }

    public static class ZipException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ZipException(final Exception exception) {
            super(exception);
        }
    }

}
