package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryAbstractTest;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;

class DataMatrixCellBasedRepositoryTest extends DataMatrixRepositoryAbstractTest<DataResourceKey, Object> {

    @Override
    protected DataMatrixRepositoryTestContext<DataResourceKey, Object> initializeContext() {
        return new DataMatrixCellBasedRepositoryTestContext();
    }
}