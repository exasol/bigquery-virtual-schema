package com.exasol.adapter.dialects.bigquery.util;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.exasol.bucketfs.Bucket;
import com.exasol.bucketfs.BucketAccessException;

public class BucketFsFolder {

    private static final String trailingSlash(final String folder) {
        return folder.endsWith("/") ? folder : folder + "/";
    }

    static final String noTrailingSlash(final String folder) {
        return folder.replaceFirst("/$", "");
    }

    private static final Logger LOGGER = Logger.getLogger(BucketFsFolder.class.getName());

    private final Bucket bucket;
    private final String folder;
    private List<String> content;

    public BucketFsFolder(final Bucket bucket, final String folder) {
        this.bucket = bucket;
        this.folder = trailingSlash(folder);
    }

    /**
     * Won't delete a folder, at least not if folder is not empty
     *
     * @throws BucketAccessException
     */
    public void deleteFile() throws BucketAccessException {
        this.bucket.deleteFileNonBlocking(noTrailingSlash(this.folder));
    }

    public void deleteFolder() throws BucketAccessException {
        deleteFile();
        for (final String file : getContent()) {
            final String path = this.folder + file;
            LOGGER.fine("Deleting file " + path);
            this.bucket.deleteFileNonBlocking(path);
        }
    }

    public List<String> getContent() throws BucketAccessException {
        if (this.content != null) {
            return this.content;
        }
        final List<String> all = this.bucket.listContents();
        this.content = (all.contains(noTrailingSlash(this.folder))
                ? this.bucket.listContents(trailingSlash(this.folder))
                : Collections.emptyList());
        return this.content;
    }

    public List<String> fullPaths() throws BucketAccessException {
        final String prefix = "/" + this.bucket.getFullyQualifiedBucketName() + "/";
        return getContent().stream() //
                .map(s -> prefix + this.folder + s) //
                .collect(Collectors.toList());
    }

    public boolean contains(final Path path) throws BucketAccessException {
        return getContent().contains(path.getFileName().toString());
    }
}
