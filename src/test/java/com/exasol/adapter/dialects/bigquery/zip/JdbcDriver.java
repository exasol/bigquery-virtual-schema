package com.exasol.adapter.dialects.bigquery.zip;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

public class JdbcDriver {

    private static final Pattern FILENAME = Pattern.compile("([^/]*)\\.[^.]*$");

    private static final String removeTrailingSlash(final String folder) {
        return folder.replaceFirst("/$", "");
    }

    private String sourceUrl;
    private String filename;
    private Path localFolder;
    @Getter
    private String bucketFsFolder;

    public JdbcDriver withSourceUrl(final String value) {
        this.sourceUrl = value;
        final Matcher matcher = FILENAME.matcher(value);
        if (!matcher.find()) {
            throw new RuntimeException("Could not find filename without extension in URL '" + value + "'");
        }
        this.filename = matcher.group(1);
        return this;
    }

    public JdbcDriver withFilename(final String value) {
        this.filename = value;
        return this;
    }

    public JdbcDriver withLocalFolder(final String value) {
        this.localFolder = Paths.get(value);
        return this;
    }

    public JdbcDriver withExasolBucketFsFolder(final String value) {
        this.bucketFsFolder = removeTrailingSlash(value);
        return this;
    }

    public Path getLocalCopy() {
        return this.localFolder.resolve(this.filename);
    }

    public String getDownloadUrl() {
        return this.sourceUrl;
    }

    public String getPathInBucketFs(final Path path) {
        return this.bucketFsFolder + "/" + path.getFileName().toString();
    }

}
