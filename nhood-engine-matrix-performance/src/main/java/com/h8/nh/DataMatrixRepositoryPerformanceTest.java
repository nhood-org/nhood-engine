package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;

public class DataMatrixRepositoryPerformanceTest
        extends DataMatrixRepositoryAbstractPerformanceTest<DataResourceKey, Object> {

    @Override
    protected final DataMatrixRepositoryTestContext<DataResourceKey, Object> initializeContext() {
        return new DataMatrixRepositoryPerformanceTestContext(this.getMetadataSize());
    }
}
