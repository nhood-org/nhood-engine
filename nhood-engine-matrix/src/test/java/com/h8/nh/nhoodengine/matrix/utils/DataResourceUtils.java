package com.h8.nh.nhoodengine.matrix.utils;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.impl.DataMatrixCellResource;

import java.util.UUID;

public final class DataResourceUtils {

    private DataResourceUtils() {
    }

    public static DataResource<DataResourceKey, Object> resource(DataResourceKey key) {
        return new DataResource<>(key, UUID.randomUUID());
    }

    public static DataMatrixCellResource<DataResourceKey> matrixCellResource(DataResourceKey key) {
        return new DataMatrixCellResource<>(UUID.randomUUID(), key);
    }
}
