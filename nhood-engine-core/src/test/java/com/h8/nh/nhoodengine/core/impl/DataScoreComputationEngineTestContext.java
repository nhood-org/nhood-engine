package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.impl.DataMatrixCellBasedRepository;
import org.assertj.core.api.Assertions;

import java.util.Arrays;

public final class DataScoreComputationEngineTestContext
        implements DataFinderTestContext<DataResourceKey, Object> {

    private static final int METADATA_SIZE = 3;

    private final DataMatrixRepository<DataResourceKey, Object> repository;

    private int registered;

    DataScoreComputationEngineTestContext() {
        this.repository = new DataMatrixCellBasedRepository<>(METADATA_SIZE);
    }

    @Override
    public DataFinder<DataResourceKey, Object> initializeDataFinder() {
        return new DataScoreComputationEngine<>(repository);
    }

    @Override
    public void register(DataResource<DataResourceKey, Object> data) {
        try {
            repository.add(data);
            registered++;
        } catch (DataMatrixRepositoryFailedException e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage(), e);
        }
    }

    @Override
    public DataResource<DataResourceKey, Object> getResource(DataResourceKey key) {
        try {
            return repository.findNeighbours(key)
                    .next()
                    .stream()
                    .filter(r -> Arrays.equals(key.unified(), r.getKey().unified()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
        } catch (DataMatrixRepositoryFailedException e) {
            Assertions.fail("Unexpected exception thrown: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public int registeredDataSize() {
        return registered;
    }

    @Override
    public DataResourceKey dataKey(DataResourceKey key) {
        return key;
    }

    @Override
    public Object data(DataResourceKey key) {
        return Arrays.toString(key.unified());
    }
}
