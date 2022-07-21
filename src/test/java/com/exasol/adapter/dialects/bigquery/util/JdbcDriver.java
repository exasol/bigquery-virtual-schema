package com.exasol.adapter.dialects.bigquery.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdbcDriver {

    private static final Pattern FILENAME = Pattern.compile("([^/]*)\\.[^.]*$");
    // private static final Pattern FILENAME_WITH_EXTENSION = Pattern.compile("([^/]*)$");

    private String sourceUrl;
    private String filename;
    private Path localFolder;

    public JdbcDriver withSourceUrl(final String value) {
        this.sourceUrl = value;
        final Matcher matcher = FILENAME.matcher(value);
        if (!matcher.find()) {
            throw new RuntimeException("Could not find filename without extension in URL '" + value + "'");
        }
        this.filename = matcher.group(1);
        // //
        // matcher = FILENAME_WITH_EXTENSION.matcher(value);
        // if (!matcher.find()) {
        // throw new RuntimeException("Could not find filename with extension in URL '" + value + "'");
        // }
        // this.filenameWithExtension = matcher.group(1);
        return this;
    }

    public Path getLocalFolder() {
        return localFolder;
    }

    public JdbcDriver withLocalFolder(final String value) {
        this.localFolder = Paths.get(value);
        return this;
    }

    public Path getLocalCopy() {
        return this.localFolder.resolve(this.filename);
    }

    public String getDownloadUrl() {
        return this.sourceUrl;
    }

    public String getBucketFsFolder() {
        return this.filename;
    }

    public String getPathInBucketFs(final Path path) {
        return getBucketFsFolder() + "/" + path.getFileName().toString();
    }

}
