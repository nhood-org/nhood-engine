package com.h8.nh.nhoodengine.core;

/**
 * This interface defines thread safety requirements for DataFinder interface
 */
public interface DataFinderThreadSafeRequirements {

    void shouldHandleMultipleConcurrentSearches()
            throws InterruptedException;

    void shouldHandleMultipleConcurrentSearchesAndDataGrowth()
            throws InterruptedException;
}
