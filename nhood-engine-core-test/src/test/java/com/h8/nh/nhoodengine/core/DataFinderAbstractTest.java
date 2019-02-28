package com.h8.nh.nhoodengine.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Vector;

public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private DataFinder<K, D> dataFinder;

    protected abstract void registerData(Vector<K> key, D data);

    protected abstract DataFinder<K, D> initializeDataFinder();

    @BeforeEach
    void setUp() {
        dataFinder = initializeDataFinder();
    }

    @Override @Test
    public final void shouldThrowAnExceptionWhenCriteriaIsNull() {

    }

    @Override @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsNull() {

    }

    @Override @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch() {

    }

    @Override @Test
    public final void shouldThrowAnExceptionWhenCriteriaLimitIsNegative() {

    }

    @Override @Test
    public final void shouldReturnAnEmptyResultListWhenCriteriaLimitZero() {

    }

    @Override @Test
    public final void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize() {

    }

    @Override @Test
    public final void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize() {

    }

    @Override @Test
    public final void shouldReturnListOfClosestResultForAGivenMetadataVector() {

    }

    @Override @Test
    public final void shouldReturnListOfClosestResultForALowestPossibleMetadataVector() {

    }

    @Override @Test
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector() {

    }

    @Override @Test
    public final void shouldReturnListOfClosestResultForAAllZeroesMetadataVector() {

    }
}
