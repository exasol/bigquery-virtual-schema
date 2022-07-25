package com.exasol.adapter.dialects.bigquery.util;

import com.exasol.bucketfs.Bucket;

public class BucketFsFolder {

    static final String noTrailingSlash(final String folder) {
        return folder.replaceFirst("/$", "");
    }

    private final Bucket bucket;
    private final String folder;

    public BucketFsFolder(final Bucket bucket, final String folder) {
        this.bucket = bucket;
        this.folder = noTrailingSlash(folder);
    }

    public String getUdfPath(final String fileName) {
        return "/buckets/" + bucket.getFullyQualifiedBucketName() + "/" + folder + "/" + fileName;
    }
}
