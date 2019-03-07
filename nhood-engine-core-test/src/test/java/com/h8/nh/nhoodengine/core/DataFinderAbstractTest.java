package com.h8.nh.nhoodengine.core;


import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private static final Vector<Integer> KEY_VECTOR_MIN_LIMIT = new Vector<>(Arrays.asList(-1000, -100, -10));

    private static final Vector<Integer> KEY_VECTOR_MAX_LIMIT = new Vector<>(Arrays.asList(10, 100, 1000));

    private DataFinder<K, D> dataFinder;

    private List<DataResource<K, D>> data;

    @BeforeEach
    public final void setUp() {
        dataFinder = initializeDataFinder();
        data = DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(this::toDataKey)
                .map(k -> DataResource.builder(getDataKeyClass(), getDataClass())
                        .key(k)
                        .data(toData(k))
                        .build())
                .collect(Collectors.toList());
        data.forEach(this::register);
    }

    protected abstract Class<K> getDataKeyClass();

    protected abstract Class<D> getDataClass();

    protected abstract DataFinder<K,D> initializeDataFinder();

    protected abstract Vector<K> toDataKey(Vector<Integer> key);

    protected abstract D toData(Vector<K> key);

    protected abstract void register(DataResource<K, D> data);

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
                .metadata(dataKey())
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
        Vector<K> metadata = dataKey(0, 0);
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
        Vector<K> metadata = dataKey(0, 0, 0);
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
        Vector<K> metadata = dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(0)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assertThat(results).isEmpty();
    }

    @Override
    @Test
    public final void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assertThat(results).hasSize(criteria.getLimit());
    }

    @Override
    @Test
    public final void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(Integer.MAX_VALUE)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assertThat(results).hasSize(data.size());
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(5, -50, 500);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        // TODO!!!
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForALowestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        // TODO!!!
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        // TODO!!!
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAAllZeroesMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        // TODO!!!
    }

    private Vector<K> dataKey(Integer... values) {
        Vector<Integer> key = new Vector<>(Arrays.asList(values));
        return toDataKey(key);
    }

    private D data(Integer... values) {
        Vector<K> key = dataKey(values);
        return toData(key);
    }
}
