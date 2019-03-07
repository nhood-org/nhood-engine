package com.h8.nh.nhoodengine.core;


import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    protected abstract Class<D> getDataClass();

    protected abstract Class<K> getDataKeyClass();

    protected abstract DataFinder<K,D> initializeDataFinder();

    protected abstract Vector<K> toDataKey(Vector<Integer> key);

    protected abstract D toData(Vector<K> key);

    protected abstract void register(Vector<K> key, D data);

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaIsNull() {
        assertThatThrownBy(() -> dataFinder.find(null))
                .isInstanceOf(DataFinderFailedException.class)
                .hasMessage("DataFinderCriteria may not be null")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsNull() {
        Class<K> keyClass = getDataKeyClass();
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(null)
                .limit(10)
                .build();
        assertThatThrownBy(() -> dataFinder.find(criteria))
                .isInstanceOf(DataFinderFailedException.class)
                .hasMessage("DataFinderCriteria metadata may not be null")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorIsEmpty() {
        Class<K> keyClass = getDataKeyClass();
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(vector())
                .limit(10)
                .build();
        assertThatThrownBy(() -> dataFinder.find(criteria))
                .isInstanceOf(DataFinderFailedException.class)
                .hasMessage("DataFinderCriteria metadata may not be empty")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch() {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = vector(1, 1);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        assertThatThrownBy(() -> dataFinder.find(criteria))
                .isInstanceOf(DataFinderFailedException.class)
                .hasMessage("DataFinderCriteria metadata size does not match data in repository")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldThrowAnExceptionWhenCriteriaLimitIsNegative() {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = vector(1, 1, 1);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(-1)
                .build();
        assertThatThrownBy(() -> dataFinder.find(criteria))
                .isInstanceOf(DataFinderFailedException.class)
                .hasMessage("DataFinderCriteria limit may not be negative")
                .hasNoCause();
    }

    @Override
    @Test
    public final void shouldReturnAnEmptyResultListWhenCriteriaLimitZero()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = vector(1, 1, 1);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(0)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assertThat(results).isEmpty();
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

    private Vector<K> vector(Integer... values) {
        Vector<Integer> key = new Vector<>(Arrays.asList(values));
        return toDataKey(key);
    }
}
