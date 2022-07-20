package com.exasol.adapter.dialects.bigquery.util.zip;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * Download zip archive from URL, support direct extract from stream
 */
public class ZipDownloader {

    public static ZipDownloader monolithic(final String downloadUrl, final Path destinationFolder) {
        return new ZipDownloader(Mode.DOWNLOAD_ARCHIVE, downloadUrl, destinationFolder);
    }

    public static ZipDownloader extracting(final String downloadUrl, final Path destinationFolder) {
        return new ZipDownloader(Mode.EXTRACT_TO_LOCAL_FOLDER, downloadUrl, destinationFolder);
    }

    private enum Mode {
        DOWNLOAD_ARCHIVE("([^/]*)$"), //
        EXTRACT_TO_LOCAL_FOLDER("([^/]*)\\.[^.]*$");

        final Pattern pattern;

        private Mode(final String filenameRegexp) {
            this.pattern = Pattern.compile(filenameRegexp);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(ZipDownloader.class.getName());
//    private static final Pattern FILENAME = Pattern.compile("([^/]*)$");

    private final Mode mode;
    private final String downloadUrl;
    @Getter
    private final String filename;
    private final Path localCopy;

    public ZipDownloader(final Mode mode, final String downloadUrl, final Path destinationFolder) {
        this.mode = mode;
        this.downloadUrl = downloadUrl;
        final Matcher matcher = mode.pattern.matcher(downloadUrl);
        if (!matcher.find()) {
            throw new RuntimeException("Could not find filename in URL '" + downloadUrl + "'");
        }
        this.filename = matcher.group(1);
        this.localCopy = destinationFolder.resolve(this.filename);
    }

    public List<Path> inventory(final String globPattern) throws IOException {
        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
        return pathStream() //
                .filter(p -> matcher.matches(p.getFileName())) //
                .collect(Collectors.toList());
    }

    private Stream<Path> pathStream() throws IOException {
        if (this.mode == Mode.EXTRACT_TO_LOCAL_FOLDER) {
            return Files.list(this.localCopy);
        }
        return new ZipInventory().entries(this.localCopy) //
                .stream() //
                .map(this.localCopy::resolve);
    }

//    public Stream<Path> pathStreamFromZipArchive(final PathMatcher matcher) throws IOException {
//        return new ZipInventory().entries(this.localCopy) //
//                .stream() //
//                .map(this.localCopy::resolve);
//    }
//
//    public Stream<Path> pathStreamFromFileSystem(final PathMatcher matcher) throws IOException {
//        return Files.list(this.localCopy);
//    }
//
//    public List<Path> inventoryFromFilesystem(final String globPattern) throws IOException {
//        final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
//        return Files.list(this.localCopy) //
//                .filter(p -> matcher.matches(p.getFileName())) //
//                .collect(Collectors.toList());
//    }

    public boolean localCopyExists() {
        return Files.exists(this.localCopy);
    }

    public Path getLocalCopy() {
        return this.localCopy;
    }

    public void download() throws IOException, URISyntaxException {
        final URL remote = new URI(this.downloadUrl).toURL();
        LOGGER.info("Download " + remote + " to " + this.localCopy);
        try (InputStream input = remote.openStream()) {
            switch (this.mode) {
            case EXTRACT_TO_LOCAL_FOLDER:
                new ZipExtractor(this.localCopy).extract(input);
                break;
            default:
                Files.copy(input, getLocalCopy());
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
