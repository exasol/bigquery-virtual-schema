package com.exasol.adapter.dialects.bigquery.zip;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;

public class BucketFsInventory {

    private final String folder;
    private final List<String> content;

    private static final String ensureTrailingSlash(final String folder) {
        return folder.endsWith("/") ? folder : folder + "/";
    }

    public BucketFsInventory(final Bucket bucket, final String folder) throws BucketAccessException {
        this.folder = ensureTrailingSlash(folder);
        this.content = bucket.listContents(this.folder);
    }

    public List<String> filenames() {
        return this.content;
    }

    public List<String> fullPaths() {
        return this.content.stream() //
                .map(s -> "/bfsdefault/default/" + this.folder + s) //
                .collect(Collectors.toList());
    }

    public boolean contains(final Path path) {
        return this.content.contains(path.getFileName().toString());
    }
}
