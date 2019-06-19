package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryAbstractThreadSafeTest;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;

class DataMatrixCellBasedRepositoryThreadSafeTest
        extends DataMatrixRepositoryAbstractThreadSafeTest<DataResourceKey, Object> {

    @Override
    protected DataMatrixRepositoryTestContext<DataResourceKey, Object> initializeContext() {
        return new DataMatrixCellBasedRepositoryTestContext();
    }
}
