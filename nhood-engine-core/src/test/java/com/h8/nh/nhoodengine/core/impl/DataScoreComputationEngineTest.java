package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderAbstractTest;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCell;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellConfiguration;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellFactory;
import com.h8.nh.nhoodengine.utils.DataFinderTestContext;

import java.util.Arrays;

class DataScoreComputationEngineTest extends DataFinderAbstractTest<DataResourceKey, Object> {

    @Override
    protected DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new TestContext();
    }

    private static class TestContext implements DataFinderTestContext<DataResourceKey, Object> {

        private final DataMatrixCell<DataResource<DataResourceKey, Object>> cell;

        private int registered;

        TestContext() {
            DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder().build();
            this.cell = DataMatrixCellFactory.root(3, configuration);
        }

        @Override
        public DataFinder<DataResourceKey, Object> initializeDataFinder() {
            return new DataScoreComputationEngine<>(cell);
        }

        @Override
        public void register(DataResource<DataResourceKey, Object> data) {
            cell.add(data);
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
}