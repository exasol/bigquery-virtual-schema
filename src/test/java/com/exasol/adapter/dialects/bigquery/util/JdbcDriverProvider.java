package com.exasol.adapter.dialects.bigquery.util;

import static java.util.stream.Collectors.toList;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;

class JdbcDriverProvider {
    private static final Logger LOGGER = Logger.getLogger(JdbcDriverProvider.class.getName());
    private static final Pattern FILENAME_WITH_EXTENSION = Pattern.compile("([^/]*)$");
    private final Bucket bucket;

    JdbcDriverProvider(final Bucket bucket) {
        this.bucket = bucket;
    }

    List<String> uploadJdbcDriverToBucketFs(final String jdbcDriverUrl) {
        final String zipFileName = getFileName(jdbcDriverUrl);
        final Path localFile = Paths.get("target").resolve(zipFileName).toAbsolutePath();

        download(jdbcDriverUrl, localFile);

        uploadToBucketFs(localFile, zipFileName);

        return listZipContent(localFile).stream() //
                .filter(name -> name.toLowerCase().endsWith(".jar")) //
                .map(name -> getUdfPath(getFileNameWithoutExtension(zipFileName), name)) //
                .collect(toList());
    }

    private void uploadToBucketFs(final Path localFile, final String fileName) {
        try {
            if (!this.bucket.listContents().contains(fileName)) {
                final String pathInBucket = "/" + fileName;
                LOGGER.fine(() -> "Uploading " + localFile + " to bucket at " + pathInBucket);
                this.bucket.uploadFile(localFile, pathInBucket);
            }
        } catch (FileNotFoundException | BucketAccessException | TimeoutException exception) {
            throw new IllegalStateException("Error uploading to bucketfs", exception);
        }
    }

    private String getUdfPath(final String folder, final String fileName) {
        return "/buckets/" + this.bucket.getFullyQualifiedBucketName() + "/" + folder + "/" + fileName;
    }

    private List<String> listZipContent(final Path localFile) {
        try (ZipFile zip = ZipFile.builder().setPath(localFile).get()) {
            return StreamSupport
                    .stream(Spliterators.spliteratorUnknownSize(zip.getEntries().asIterator(), Spliterator.ORDERED),
                            false)
                    .map(ZipArchiveEntry::getName).collect(toList());
        } catch (final IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    private String getFileName(final String jdbcDriverUrl) {
        final Matcher matcher = FILENAME_WITH_EXTENSION.matcher(jdbcDriverUrl);
        if (!matcher.find()) {
            throw new RuntimeException("Could not find filename without extension in URL '" + jdbcDriverUrl + "'");
        }
        return matcher.group(1);
    }

    private String getFileNameWithoutExtension(final String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    private void download(final String downloadUrl, final Path localCopy) {
        if (Files.exists(localCopy)) {
            LOGGER.info("File " + localCopy + " already exists, no need to download it");
            return;
        }
        try {
            final URL remote = new URI(downloadUrl).toURL();
            LOGGER.info("Download " + remote + " to " + localCopy);
            try (InputStream input = remote.openStream()) {
                Files.copy(input, localCopy);
            }
        } catch (URISyntaxException | IOException exception) {
            throw new IllegalStateException("Error downloading file from " + downloadUrl, exception);
        }
    }
}
