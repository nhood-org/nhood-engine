package com.h8.nh.nhoodengine.core;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Vector;

public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private static final int KEY_VECTOR_SIZE = 3;

    private static final Vector<Integer> KEY_VECTOR_MIN_LIMIT = new Vector<>(Arrays.asList(-1000, -100, -10));

    private static final Vector<Integer> KEY_VECTOR_MAX_LIMIT = new Vector<>(Arrays.asList(10, 100, 1000));


    private DataFinder<K, D> dataFinder;

    protected abstract Vector<K> mapDataKey(Vector<Integer> key);

    protected abstract D mapDataKeyToData(Vector<K> key);

    protected abstract void registerDataResource(Vector<K> key, D data);

    protected abstract DataFinder<K, D> initializeDataFinder();

    @BeforeAll
    void setUp() {
        dataFinder = initializeDataFinder();
        for (int i = 0; i < KEY_VECTOR_SIZE; i++) {
            
        }
    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaIsNull() {

    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsNull() {

    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch() {

    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaLimitIsNegative() {

    }

    @Override
    @Test
    public final void shouldReturnAnEmptyResultListWhenCriteriaLimitZero() {

    }

    @Override
    @Test
    public final void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize() {

    }

    @Override
    @Test
    public final void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize() {

    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAGivenMetadataVector() {

    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForALowestPossibleMetadataVector() {

    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector() {

    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAAllZeroesMetadataVector() {

    }
}
