package com.h8.nh.nhoodengine.core;


import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Vector;

public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private static final Vector<Integer> KEY_VECTOR_MIN_LIMIT = new Vector<>(Arrays.asList(-1000, -100, -10));

    private static final Vector<Integer> KEY_VECTOR_MAX_LIMIT = new Vector<>(Arrays.asList(10, 100, 1000));

    private DataFinder<K, D> dataFinder;

    @BeforeEach
    public final void setUp() {
        dataFinder = initializeDataFinder();
        DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(this::toDataKey)
                .forEach(k -> register(k, toData(k)));
    }

    protected abstract DataFinder<K,D> initializeDataFinder();

    protected abstract Vector<K> toDataKey(Vector<Integer> key);

    protected abstract D toData(Vector<K> key);

    protected abstract void register(Vector<K> key, D data);

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaIsNull() {
        dataFinder.find(null);
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
