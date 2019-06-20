package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;
import com.h8.nh.nhoodengine.matrix.impl.DataMatrixCellBasedRepository;

import java.util.Arrays;

public final class DataMatrixRepositoryPerformanceTestContext
        implements DataMatrixRepositoryTestContext<DataResourceKey, Object> {

    private final int metadataSize;

    DataMatrixRepositoryPerformanceTestContext(final int metadataSize) {
        this.metadataSize = metadataSize;
    }

    @Override
    public DataMatrixRepository<DataResourceKey, Object> initializerRepository() {
        return new DataMatrixCellBasedRepository<>(metadataSize);
    }

    @Override
    public DataResourceKey dataKey(final DataResourceKey key) {
        return key;
    }

    @Override
    public Object data(final DataResourceKey key) {
        return Arrays.toString(key.unified());
    }
}
