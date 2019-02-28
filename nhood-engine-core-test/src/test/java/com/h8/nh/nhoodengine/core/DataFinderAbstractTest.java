package com.h8.nh.nhoodengine.core;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private static final int KEY_VECTOR_SIZE = 3;

    private static final Vector<Integer> KEY_VECTOR_MIN_LIMIT = new Vector<>(Arrays.asList(-1000, -100, -10));

    private static final Vector<Integer> KEY_VECTOR_MAX_LIMIT = new Vector<>(Arrays.asList(10, 100, 1000));

    private DataFinder<K, D> dataFinder;

    protected abstract Vector<K> mapDataKey(Vector<Integer> key);

    protected abstract D mapDataKeyToData(Vector<K> key);

    protected abstract void registerDataResource(Vector<K> key, D data);

    protected abstract DataFinder<K, D> initializeDataFinder();

    @BeforeEach
    public final void setUp() {
        dataFinder = initializeDataFinder();
        initializeData();
    }

    private void initializeData() {
        generateKeys().map(this::mapDataKey)
                .forEach(k -> registerDataResource(k, mapDataKeyToData(k)));
    }

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

    private Stream<Vector<Integer>> generateKeys() {
        Stream<Vector<Integer>> keys = generateFirstLevelKeys();
        for (int i = 1; i < KEY_VECTOR_SIZE; i++) {
            keys = generateNextLevelKeys(keys, i);
        }
        return keys;
    }

    private Stream<Vector<Integer>> generateFirstLevelKeys() {
        return IntStream.range(KEY_VECTOR_MIN_LIMIT.get(0), KEY_VECTOR_MAX_LIMIT.get(0))
                .mapToObj(Vector::new);
    }

    private Stream<Vector<Integer>> generateNextLevelKeys(Stream<Vector<Integer>> previousLevelKeys, int level) {
        return previousLevelKeys
                .flatMap(v -> generateNextLevelKeys(v, level));
    }

    private Stream<Vector<Integer>> generateNextLevelKeys(Vector<Integer> previousLevelKey, int level) {
        return IntStream
                .range(KEY_VECTOR_MIN_LIMIT.get(level), KEY_VECTOR_MAX_LIMIT.get(level))
                .mapToObj(i -> generateNextLevelKey(previousLevelKey, i));
    }

    private Vector<Integer> generateNextLevelKey(Vector<Integer> previousLevelKey, Integer nextValue) {
        List<Integer> nextLevelKey = new ArrayList<>(previousLevelKey);
        nextLevelKey.add(nextValue);
        return new Vector<>(nextLevelKey);
    }
}
