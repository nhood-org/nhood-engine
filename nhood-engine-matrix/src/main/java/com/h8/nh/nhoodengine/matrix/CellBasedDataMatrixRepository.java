package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.model.DataMatrixCell;
import com.h8.nh.nhoodengine.matrix.model.DataMatrixCellConfiguration;
import com.h8.nh.nhoodengine.matrix.model.DataMatrixCellFactory;
import com.h8.nh.nhoodengine.matrix.model.DataMatrixCellIterator;

public final class CellBasedDataMatrixRepository implements DataMatrixRepository<DataResourceKey, Object> {

    private final int metadataSize;

    private final DataMatrixCell<DataResource<DataResourceKey, Object>> cell;

    public CellBasedDataMatrixRepository(
            final int metadataSize) {
        this(metadataSize, DataMatrixCellConfiguration.builder().build());
    }

    public CellBasedDataMatrixRepository(
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
    public void add(
            final DataResource<DataResourceKey, Object> resource) {
        cell.add(resource);
    }

    @Override
    public DataMatrixResourceIterator<DataResourceKey, Object> findNeighbours(
            final DataResourceKey metadata) {
        return DataMatrixCellIterator.startWith(metadata.unified(), cell);
    }
}
