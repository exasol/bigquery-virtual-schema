package com.exasol.adapter.dialects.bigquery.zip;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;

public class ZipInventory extends ZipTraverser<ZipInventory> {

    private final Set<String> inventory = new TreeSet<>();
    private static final Path VIRTUAL_FOLDER = Paths.get("");

    public ZipInventory() {
        super(VIRTUAL_FOLDER);
    }

    public Set<String> entries(final Path archive) throws IOException {
        traverse(archive);
        return this.inventory;
    }

    @Override
    protected long processZipEntry(final ZipEntry zipEntry) throws IOException {
        this.inventory.add(zipEntry.getName());
        return 0;
    }

    @Override
    protected ZipInventory getThis() {
        return this;
    }

}
