package com.exasol.adapter.dialects.bigquery.util.zip;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;

public class ZipExtractor extends ZipTraverser<ZipExtractor> {

    public ZipExtractor(final File destinationFolder) {
        this(destinationFolder.toPath());
    }

    public ZipExtractor(final Path destinationFolder) {
        super(destinationFolder);
    }

    public void extract(final Path archive) throws IOException {
        traverse(archive);
    }

    public void extract(final InputStream archive) throws IOException {
        traverse(archive);
    }

    @Override
    protected long processZipEntry(final ZipEntry zipEntry) throws IOException {
        final Path path = validatePath(zipEntry);
        createParentFoldersForArchivesCreatedOnWindowsOS(path);
        if (!zipEntry.isDirectory()) {
            return extractSingleFile(zipEntry, path);
        }

        // Method extractSingleFile() will create all parent folders for extracted files.
        // The following code is needed to create empty folders, too.
        if (!Files.isDirectory(path)) {
            Files.createDirectories(path);
        }
        return 0;
    }

    private long extractSingleFile(final ZipEntry zipEntry, final Path path) throws IOException {
        long totalUncompressedSize = 0;
        try (OutputStream stream = Files.newOutputStream(path)) {
            int len;
            while ((len = this.zip.read(this.buffer)) > 0) {
                totalUncompressedSize += len;
                final long rate = totalUncompressedSize / zipEntry.getCompressedSize();
                if (rate > this.maxCompression) {
                    throw new ExtractionAbortedException("Compression rate of entry {0} exceeds max. rate {1}.",
                            zipEntry.getName(), this.maxCompression);
                }
                stream.write(this.buffer, 0, len);
            }
        }
        return totalUncompressedSize;
    }

    private void createParentFoldersForArchivesCreatedOnWindowsOS(final Path path) throws IOException {
        final Path parent = path.getParent();
        if (!Files.isDirectory(parent)) {
            Files.createDirectories(parent);
        }
    }

    @Override
    protected ZipExtractor getThis() {
        return this;
    }
}
