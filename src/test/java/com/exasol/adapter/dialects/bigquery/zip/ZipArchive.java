package com.exasol.adapter.dialects.bigquery.zip;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;

public class ZipArchive {

    public static Path extract(final Path archive, final Path destinationFolder) throws IOException {
        new ZipExtractor(destinationFolder).extract(archive);
        return destinationFolder;
    }

    protected final byte[] buffer = new byte[1024];
    protected final Path folder;

    public ZipArchive(final Path folder) {
        this.folder = folder.toAbsolutePath().normalize();
    }

    protected String entryName(final ZipEntry zipEntry, final Path path) {
        return this.folder.relativize(path).toString().replace("\\", "/") + (zipEntry.isDirectory() ? "/" : "");
    }
}
