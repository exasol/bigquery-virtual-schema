package com.exasol.adapter.dialects.bigquery.zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class ZipTraverser<T extends ZipTraverser<T>> extends ZipArchive {

    // thresholds in order to prevent zip bomb attacks
    protected long maxSize = 1000000000; // 1 GB
    protected long maxEntries = 10000;
    protected long maxCompression = 50;

    protected ZipInputStream zip;

    ZipTraverser(final Path destinationFolder) {
        super(destinationFolder);
    }

    T withMaxSize(final long value) {
        this.maxSize = value;
        return getThis();
    }

    T withMaxEntries(final long value) {
        this.maxEntries = value;
        return getThis();
    }

    T withMaxCompression(final long value) {
        this.maxCompression = value;
        return getThis();
    }

    protected T traverse(final Path archive) throws IOException {
        try (InputStream stream = Files.newInputStream(archive)) {
            return traverse(stream);
        }
    }

    protected T traverse(final InputStream archive) throws IOException {
        // sonar proposes to use ZipFile and zipFile.getInputStream(ze)
        try (ZipInputStream stream = new ZipInputStream(archive)) {
            this.zip = stream;
            ZipEntry entry;
            long entries = 0;
            long size = 0;
            while ((entry = stream.getNextEntry()) != null) {
                try {
                    size += processZipEntry(entry);
                } catch (final StopTraversalException e) {
                    return getThis();
                }
                if (size > this.maxSize) {
                    throw new ExtractionAbortedException("Size of extracted data exceeds max. of {0} bytes.",
                            this.maxSize);
                }
                if (++entries > this.maxEntries) {
                    throw new ExtractionAbortedException("Number of entries exceeds max. {0}.", this.maxEntries);
                }
            }
            stream.closeEntry();
        }
        return getThis();
    }

    public static class ExtractionAbortedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        ExtractionAbortedException(final String pattern, final Object... arguments) {
            this(MessageFormat.format("{0} {1}", MessageFormat.format(pattern, arguments), "Aborting extraction."));
        }

        ExtractionAbortedException(final String message) {
            super(message);
        }
    }

    protected static class StopTraversalException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    protected Path validatePath(final ZipEntry entry) throws IOException {
        final Path path = this.folder.resolve(entry.getName());
        if (path.startsWith(this.folder)) {
            return path;
        }
        throw new IOException("Entry is outside of the target dir: " + entry.getName());
    }

    protected abstract long processZipEntry(ZipEntry zipEntry) throws IOException, StopTraversalException;

    protected abstract T getThis();
}
