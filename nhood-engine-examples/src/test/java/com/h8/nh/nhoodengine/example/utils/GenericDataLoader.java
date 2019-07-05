package com.h8.nh.nhoodengine.example.utils;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.Scanner;

public abstract class GenericDataLoader<K extends DataResourceKey, D> {

    private final DataMatrixRepository<K, D> repository;

    public GenericDataLoader(
            final DataMatrixRepository<K, D> repository) {
        this.repository = repository;
    }

    protected abstract String getResourceFile();

    protected abstract String getResourceFileDelimiter();

    protected abstract void mapHeaders(final String[] headersRow);

    protected abstract DataResource<K, D> mapRow(final String[] row);

    public void load()
            throws DataMatrixRepositoryFailedException {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(getResourceFile());

        if (in == null) {
            throw new IllegalStateException("Could not load resource file");
        }

        try(Scanner scanner = new Scanner(in)) {
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Resource file is empty");
            }
            mapHeaders(scanner.nextLine().split(getResourceFileDelimiter()));

            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(getResourceFileDelimiter());
                repository.add(mapRow(row));
            }
        }
    }

    protected double mapDoubleValue(final String value) {
        String s = mapStringValue(value);
        if ("".equals(s)) {
            return Double.MIN_VALUE;
        } else {
            return Double.valueOf(s);
        }
    }

    protected String mapStringValue(final String value) {
        return value.replace("\"", "");
    }
}
