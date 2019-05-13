package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.DataResource;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 */
public final class DataMatrixCellFactory {

    // TODO!!! move to configuration
    private static final int SCALE = 4;

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
                .divide(BigDecimal.valueOf(2.0), SCALE, RoundingMode.CEILING);
    }

    private static BigDecimal getRootClosure(
            final DataMatrixCellConfiguration configuration) {
        return configuration.getRootRange()
                .divide(BigDecimal.valueOf(2.0), SCALE, RoundingMode.CEILING);
    }
}
