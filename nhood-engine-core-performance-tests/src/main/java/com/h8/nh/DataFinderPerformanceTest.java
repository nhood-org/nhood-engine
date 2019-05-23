package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.impl.DataScoreComputationEngine;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCell;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellConfiguration;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellFactory;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellIterator;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;

import java.util.Arrays;

public class DataFinderPerformanceTest extends DataFinderAbstractPerformanceTest<DataResourceKey, Object> {

    @Override
    protected final DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new TestContext();
    }

    private static class TestContext implements DataFinderTestContext<DataResourceKey, Object> {

        private TestContextRepository repository;

        private int registered;

        TestContext() {
            this.repository = new TestContextRepository();
        }

        @Override
        public DataFinder<DataResourceKey, Object> initializeDataFinder() {
            return new DataScoreComputationEngine<>(repository);
        }

        @Override
        public void register(
                final DataResource<DataResourceKey, Object> data) {
            repository.add(data);
            registered++;
        }

        @Override
        public DataResource<DataResourceKey, Object> getResource(
                final DataResourceKey key) {
            return repository.findNeighbours(key)
                    .next()
                    .stream()
                    .filter(r -> Arrays.equals(key.unified(), r.getKey().unified()))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
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

    private static class TestContextRepository implements DataMatrixRepository<DataResourceKey, Object> {

        private static final int METADATA_SIZE = 3;

        private final DataMatrixCell<DataResource<DataResourceKey, Object>> cell;

        TestContextRepository() {
            DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder().build();
            this.cell = DataMatrixCellFactory.root(METADATA_SIZE, configuration);
        }

        @Override
        public int getMetadataSize() {
            return METADATA_SIZE;
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
}
