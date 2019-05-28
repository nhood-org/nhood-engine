package com.h8.nh.nhoodengine.matrix.impl.model;

import com.h8.nh.nhoodengine.core.DataResource;

import java.math.BigDecimal;

import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_ROUNDING_MODE;
import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_SCALE;

public final class DataMatrixCellFactory {

    private DataMatrixCellFactory() {
    }

    public static <R extends DataResource> DataMatrixCell<R> root(
            final int size,
            final DataMatrixCellConfiguration configuration) {
        BigDecimal[] index = new BigDecimal[size];
        for (int i = 0; i < index.length; i++) {
            index[i] = getRootIndex(configuration);
        }

        BigDecimal[] closure = new BigDecimal[size];
        for (int i = 0; i < closure.length; i++) {
            closure[i] = getRootClosure(configuration);
        }

        return new DataMatrixCell<>(index, closure, null, configuration);
    }

    private static BigDecimal getRootIndex(
            final DataMatrixCellConfiguration configuration) {
        return configuration.getRootRange()
                .negate()
                .divide(BigDecimal.valueOf(2.0), UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);
    }

    private static BigDecimal getRootClosure(
            final DataMatrixCellConfiguration configuration) {
        return configuration.getRootRange()
                .divide(BigDecimal.valueOf(2.0), UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);
    }
}
