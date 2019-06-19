package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.impl.DataScoreComputationEngine;
import com.h8.nh.nhoodengine.matrix.impl.DataMatrixCellBasedRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.Arrays;

public class DataFinderPerformanceTest extends DataFinderAbstractPerformanceTest<DataResourceKey, Object> {

    @Override
    protected final DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new TestContext(this.getMetadataSize());
    }

    private static class TestContext implements DataFinderTestContext<DataResourceKey, Object> {

        private DataMatrixRepository<DataResourceKey, Object> repository;

        private int registered;

        TestContext(final int metadataSize) {
            this.repository = new DataMatrixCellBasedRepository<>(metadataSize);
        }

        @Override
        public DataFinder<DataResourceKey, Object> initializeDataFinder() {
            return new DataScoreComputationEngine<>(repository);
        }

        @Override
        public void register(final DataResource<DataResourceKey, Object> data) {
            try {
                repository.add(data);
                registered++;
            } catch (DataMatrixRepositoryFailedException e) {
                throw new IllegalStateException("Unexpected exception thrown: " + e.getMessage(), e);
            }
        }

        @Override
        public DataResource<DataResourceKey, Object> getResource(final DataResourceKey key) {
            try {
                return repository.findNeighbours(key)
                        .next()
                        .stream()
                        .filter(r -> Arrays.equals(key.unified(), r.getKey().unified()))
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
            } catch (DataMatrixRepositoryFailedException e) {
                throw new IllegalStateException("Unexpected exception thrown: " + e.getMessage(), e);
            }
        }

        @Override
        public int registeredDataSize() {
            return registered;
        }

        @Override
        public DataResourceKey dataKey(
                final DataResourceKey key) {
            return key;
        }

        @Override
        public Object data(
                final DataResourceKey key) {
            return Arrays.toString(key.unified());
        }
    }
}
