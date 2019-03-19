package com.h8.nh.nhoodengine.core;

/**
 * This interface defines logic of mapping
 * of a generic metadata key type value into double value
 * representing its geometrical location.
 *
 * @param <K> a generic type of data metadata key vector.
 */
public interface DataFinderKeyMapper<K> {

    /**
     * Maps metadata key type value into double-type geometrical location
     * @param value a metadata key value
     * @return a double-type geometrical location
     */
    double map(K value);
}
