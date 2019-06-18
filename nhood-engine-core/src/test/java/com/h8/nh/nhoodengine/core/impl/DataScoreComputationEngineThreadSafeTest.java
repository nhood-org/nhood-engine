package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinderAbstractThreadSafeTest;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResourceKey;

class DataScoreComputationEngineThreadSafeTest extends DataFinderAbstractThreadSafeTest<DataResourceKey, Object> {

    @Override
    protected DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new DataScoreComputationEngineTestContext();
    }
}
