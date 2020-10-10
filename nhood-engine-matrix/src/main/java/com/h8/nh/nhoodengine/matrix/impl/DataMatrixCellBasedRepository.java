package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a default implementation {@link DataMatrixRepository} interface based on {@link DataMatrixCell}.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public final class DataMatrixCellBasedRepository<K extends DataResourceKey, D>
        implements DataMatrixRepository<K, D> {

    private final int metadataSize;

    private final DataMatrixCell<DataMatrixCellResource<K>> cell;
    private final Map<UUID, DataResource<K, D>> data;

    public DataMatrixCellBasedRepository(
            final int metadataSize) {
        this(metadataSize, DataMatrixCellConfiguration.builder().build());
    }

    public DataMatrixCellBasedRepository(
            final int metadataSize,
            final DataMatrixCellConfiguration configuration) {
        this.metadataSize = metadataSize;
        this.cell = DataMatrixCellFactory.root(metadataSize, configuration);
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public int getMetadataSize() {
        return metadataSize;
    }

    @Override
    public void add(final DataResource<K, D> resource)
            throws DataMatrixRepositoryFailedException {
        validate(resource);
        data.put(resource.getUuid(), resource);
        DataMatrixCellResource<K> r = DataMatrixCellResource.form(resource);
        cell.add(r);
    }

    @Override
    public DataResource<K, D> find(UUID uuid) {
        throw new IllegalStateException();
    }

    @Override
    public DataResource<K, D> delete(UUID uuid) {
        throw new IllegalStateException();
    }

    @Override
    public DataMatrixResourceIterator<K, D> findNeighbours(
            final DataResourceKey metadata) {
        return DataMatrixCellIterator.startWith(metadata.unified(), cell, data);
    }

    private void validate(final DataResource<K, D> resource)
            throws DataMatrixRepositoryFailedException {
        if (resource == null) {
            throw new DataMatrixRepositoryFailedException(
                    "Data resource may not be null");
        }

        if (resource.getKey() == null) {
            throw new DataMatrixRepositoryFailedException(
                    "Data resource has invalid key: null");
        }

        if (resource.getData() == null) {
            throw new DataMatrixRepositoryFailedException(
                    "Data resource has invalid data: null");
        }

        if (resource.getKey().unified().length != metadataSize) {
            String message = String.format(
                    "Data resource has invalid key size: %s. Expected: %s",
                    resource.getKey().unified().length, metadataSize);
            throw new DataMatrixRepositoryFailedException(message);
        }
    }
}
