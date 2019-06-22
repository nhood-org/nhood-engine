package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResourceKey;

public class DataFinderPerformanceTest
        extends DataFinderAbstractPerformanceTest<DataResourceKey, Object> {

    @Override
    protected final DataFinderTestContext<DataResourceKey, Object> initializeContext() {
        return new DataFinderPerformanceTestContext(this.getMetadataSize());
    }
}
