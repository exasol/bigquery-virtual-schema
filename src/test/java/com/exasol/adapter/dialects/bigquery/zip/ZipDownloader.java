package com.exasol.adapter.dialects.bigquery.zip;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.*;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Download zip file from URL and return list of entries
 */
public class ZipDownloader {

    private static final Logger LOGGER = Logger.getLogger(ZipDownloader.class.getName());

    private static final String JDBC_DOWNLOAD_URL = "https://storage.googleapis.com/simba-bq-release/jdbc/";
    private static final String FILENAME = "SimbaJDBCDriverforGoogleBigQuery42_1.2.25.1029.zip";
    private static final String TEMP_FOLDER = "target";

    public ZipDownloader() {
    }

    public List<String> inventory() throws IOException, URISyntaxException {
        final List<String> result = new ArrayList<>();
        final ZipFile zipfile = new ZipFile(download());
        final Enumeration<? extends ZipEntry> e = zipfile.entries();
        while (e.hasMoreElements()) {
            final String name = e.nextElement().getName();
            if ((name != null) && name.endsWith(".jar")) {
                LOGGER.info("adding " + name);
                result.add(name);
            }
        }
        return result;
    }

    private File download() throws IOException, URISyntaxException {
        final URL remote = new URI(JDBC_DOWNLOAD_URL).resolve(FILENAME).toURL();
        final File localCopy = new File(TEMP_FOLDER, FILENAME);
        if (localCopy.exists()) {
            return localCopy;
        }
        LOGGER.info("Download " + remote + " to " + localCopy);
        try (ReadableByteChannel input = Channels.newChannel(remote.openStream());
                FileOutputStream output = new FileOutputStream(localCopy)) {
            output.getChannel().transferFrom(input, 0, Long.MAX_VALUE);
        }
        return localCopy;
    }

}
