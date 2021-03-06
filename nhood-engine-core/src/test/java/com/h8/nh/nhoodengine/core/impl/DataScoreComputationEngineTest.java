package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinderAbstractTest;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResourceKey;

class DataScoreComputationEngineTest extends DataFinderAbstractTest<DataResourceKey, Object> {

    @Override
    protected DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new DataScoreComputationEngineTestContext();
    }
}