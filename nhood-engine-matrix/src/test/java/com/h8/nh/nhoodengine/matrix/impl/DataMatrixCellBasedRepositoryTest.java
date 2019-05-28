package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryAbstractTest;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;
import org.junit.jupiter.api.Disabled;

import java.util.Arrays;

@Disabled
class DataMatrixCellBasedRepositoryTest extends DataMatrixRepositoryAbstractTest<DataResourceKey, Object> {

    @Override
    protected DataMatrixRepositoryTestContext<DataResourceKey, Object> initializeContext() {
        return new TestContext();
    }

    private static class TestContext implements DataMatrixRepositoryTestContext<DataResourceKey, Object> {

        @Override
        public DataMatrixRepository<DataResourceKey, Object> initializerRepository() {
            return new DataMatrixCellBasedRepository<>(METADATA_SIZE);
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
}