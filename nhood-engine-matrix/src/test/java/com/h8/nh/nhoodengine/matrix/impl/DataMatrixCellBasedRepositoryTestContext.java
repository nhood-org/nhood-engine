package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;

import java.util.Arrays;

public final class DataMatrixCellBasedRepositoryTestContext
        implements DataMatrixRepositoryTestContext<DataResourceKey, Object> {

    private static final int METADATA_SIZE = 3;

    @Override
    public DataMatrixRepository<DataResourceKey, Object> initializerRepository() {
        return new DataMatrixCellBasedRepository<>(METADATA_SIZE);
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
