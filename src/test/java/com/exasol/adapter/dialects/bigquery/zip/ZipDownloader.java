package com.exasol.adapter.dialects.bigquery.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Download zip archive from URL, support direct extract from stream
 */
public class ZipDownloader {

    private enum Mode {
        DOWNLOAD_ARCHIVE, EXTRACT_TO_LOCAL_FOLDER;
    }

    private static final Logger LOGGER = Logger.getLogger(ZipDownloader.class.getName());

    final String downloadUrl;
    final Path localCopy;

    public ZipDownloader(final String downloadUrl, final Path destinationFolder) {
        this.downloadUrl = downloadUrl;
        this.localCopy = destinationFolder;
    }

    public List<Path> inventory(final String globPattern) throws IOException {
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
        return Files.list(this.localCopy) //
                .filter(p -> matcher.matches(p.getFileName())) //
                .collect(Collectors.toList());
    }

    public boolean localFolderExists() {
        return Files.exists(this.localCopy);
    }

    public void extractToLocalFolder() throws IOException, URISyntaxException {
        download(Mode.EXTRACT_TO_LOCAL_FOLDER);
    }

    private void download(final Mode mode) throws IOException, URISyntaxException {
        final URL remote = new URI(this.downloadUrl).toURL();
        LOGGER.info("Download " + remote + " to " + this.localCopy);
        try (InputStream input = remote.openStream()) {
            switch (mode) {
            case EXTRACT_TO_LOCAL_FOLDER:
                new ZipExtractor(this.localCopy).extract(input);
                break;
            default:
                Files.copy(input, this.localCopy);
                break;
            }
        }
    }

    public static class ZipException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ZipException(final Exception exception) {
            super(exception);
        }
    }

}
