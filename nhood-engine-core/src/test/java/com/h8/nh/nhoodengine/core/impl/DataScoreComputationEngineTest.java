package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderAbstractTest;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCell;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellConfiguration;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellFactory;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellIterator;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;
import com.h8.nh.nhoodengine.utils.DataFinderTestContext;

import java.util.Arrays;

class DataScoreComputationEngineTest extends DataFinderAbstractTest<DataResourceKey, Object> {

    @Override
    protected DataFinderTestContext<DataResourceKey, Object> initializeContext() {
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
        public void register(DataResource<DataResourceKey, Object> data) {
            repository.add(data);
            registered++;
        }

        @Override
        public int registerDataSize() {
            return registered;
        }

        @Override
        public Class<DataResourceKey> dataKeyClass() {
            return DataResourceKey.class;
        }

        @Override
        public DataResourceKey dataKey(DataResourceKey key) {
            return key;
        }

        @Override
        public Class<Object> dataClass() {
            return Object.class;
        }

        @Override
        public Object data(DataResourceKey key) {
            return Arrays.toString(key.unified());
        }
    }

    private static class TestContextRepository implements DataMatrixRepository<DataResourceKey, Object> {

        private final DataMatrixCell<DataResource<DataResourceKey, Object>> cell;

        TestContextRepository() {
            DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder().build();
            this.cell = DataMatrixCellFactory.root(3, configuration);
        }

        @Override
        public int getMetadataSize() {
            return 3;
        }

        @Override
        public void add(DataResource<DataResourceKey, Object> resource) {
            cell.add(resource);
        }

        @Override
        public DataMatrixResourceIterator<DataResourceKey, Object> findNeighbours(DataResourceKey metadata) {
            return DataMatrixCellIterator.startWith(metadata.unified(), cell);
        }
    }
}