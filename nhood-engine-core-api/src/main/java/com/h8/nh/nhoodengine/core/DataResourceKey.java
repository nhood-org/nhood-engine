package com.h8.nh.nhoodengine.core;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class represents a metadata vector key of data resource
 */
public interface DataResourceKey {

    /**
     * Unified BigDecimal Scale to be used across the whole library
     */
    int UNIFIED_BIG_DECIMAL_SCALE = 4;

    /**
     * Unified BigDecimal RoundingMode to be used across the whole library
     */
    RoundingMode UNIFIED_BIG_DECIMAL_ROUNDING_MODE = RoundingMode.CEILING;

    /**
     * Data metadata key vector of unified array type BigDecimal[]
     * @return actual metadata key unified value
     */
    BigDecimal[] unified();
}
