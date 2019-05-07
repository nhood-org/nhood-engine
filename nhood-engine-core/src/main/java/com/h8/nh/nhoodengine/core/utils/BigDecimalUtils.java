package com.h8.nh.nhoodengine.core.utils;

import java.math.BigDecimal;

public final class BigDecimalUtils {

    private BigDecimalUtils() {
    }

    public static BigDecimal sqrt(final BigDecimal value) {
        if (BigDecimal.ZERO.equals(value)) {
            return BigDecimal.ZERO;
        } else {
            BigDecimal x = new BigDecimal(Math.sqrt(value.doubleValue()));
            return x.add(new BigDecimal(value.subtract(x.multiply(x)).doubleValue() / (x.doubleValue() * 2.0)));
        }
    }
}
