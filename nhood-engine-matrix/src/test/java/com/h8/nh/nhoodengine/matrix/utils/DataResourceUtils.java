package com.h8.nh.nhoodengine.matrix.utils;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

public final class DataResourceUtils {

    private DataResourceUtils() {
    }

    public static DataResource<DataResourceKey, Object> resource(DataResourceKey key) {
        return new DataResource<>(key, null);
    }
}
