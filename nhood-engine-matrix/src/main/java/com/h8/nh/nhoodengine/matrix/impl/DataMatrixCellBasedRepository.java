package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;
import com.h8.nh.nhoodengine.matrix.impl.model.DataMatrixCell;
import com.h8.nh.nhoodengine.matrix.impl.model.DataMatrixCellConfiguration;
import com.h8.nh.nhoodengine.matrix.impl.model.DataMatrixCellFactory;
import com.h8.nh.nhoodengine.matrix.impl.model.DataMatrixCellIterator;

/**
 * This is a default implementation {@link DataMatrixRepository} interface based on {@link DataMatrixCell}.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public final class DataMatrixCellBasedRepository<K extends DataResourceKey, D>
        implements DataMatrixRepository<K, D> {

    private final int metadataSize;

    private final DataMatrixCell<DataResource<K, D>> cell;

    public DataMatrixCellBasedRepository(
            final int metadataSize) {
        this(metadataSize, DataMatrixCellConfiguration.builder().build());
    }

    public DataMatrixCellBasedRepository(
            final int metadataSize,
            final DataMatrixCellConfiguration configuration) {
        this.metadataSize = metadataSize;
        this.cell = DataMatrixCellFactory.root(metadataSize, configuration);
    }

    @Override
    public int getMetadataSize() {
        return metadataSize;
    }

    @Override
    public void add(final DataResource<K, D> resource)
            throws DataMatrixRepositoryFailedException {
        validate(resource);
        cell.add(resource);
    }

    @Override
    public DataMatrixResourceIterator<K, D> findNeighbours(
            final DataResourceKey metadata) {
        return DataMatrixCellIterator.startWith(metadata.unified(), cell);
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
