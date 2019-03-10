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

/**
 * DataFinderAbstractTest is an abstract test class
 * that implements DataFinderRequirements.
 *
 * This class has been abstracted in order to maintain generic approach.
 *
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 *
 * While testing a concrete implementation of DataFinder
 * an implementer has to implement a couple of initialization
 * and mapping utilities methods.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
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

    /**
     * Key type
     * @return a generic type of data metadata key vector.
     */
    protected abstract Class<K> getDataKeyClass();

    /**
     * Data type
     * @return a generic type of data resource.
     */
    protected abstract Class<D> getDataClass();

    /**
     * Creates a new instance of DataFinder which is a subject of testing.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinder.
     */
    protected abstract DataFinder<K, D> initializeDataFinder();

    /**
     * Maps a vector of integers into a vector of metadata of generic type K.
     * @param key a vector of integers.
     * @return a mapped vector.
     */
    protected abstract Vector<K> toDataKey(Vector<Integer> key);

    /**
     * Maps a vector of metadata of generic type K into a corresponding data.
     * @param key vector of metadata of generic type K.
     * @return a corresponding data.
     */
    protected abstract D toData(Vector<K> key);

    /**
     * Registers a data as a findable resource.
     * @param data data to be registered.
     */
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
                .limit(11)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        resource(5, -50, 500),
                        resource(4, -50, 500),
                        resource(5, -49, 500),
                        resource(5, -51, 500),
                        resource(6, -50, 500),
                        resource(5, -50, 499),
                        resource(5, -50, 501),
                        resource(4, -49, 500),
                        resource(4, -51, 500),
                        resource(6, -49, 500),
                        resource(6, -51, 500)
                );
    }

    @Override
    @Test
    public final void shouldCalculateScoresOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(5, -50, 500);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(27)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .containsExactlyInAnyOrder(
                        result(resource(5, -50, 500), 0.0),

                        result(resource(4, -50, 500), 1.0),
                        result(resource(5, -49, 500), 1.0),
                        result(resource(5, -50, 499), 1.0),
                        result(resource(5, -50, 501), 1.0),
                        result(resource(5, -51, 500), 1.0),
                        result(resource(6, -50, 500), 1.0),

                        result(resource(4, -49, 500), Math.sqrt(1.0)),
                        result(resource(4, -51, 500), Math.sqrt(1.0)),
                        result(resource(4, -50, 499), Math.sqrt(1.0)),
                        result(resource(4, -50, 501), Math.sqrt(1.0)),
                        result(resource(5, -49, 499), Math.sqrt(1.0)),
                        result(resource(5, -51, 499), Math.sqrt(1.0)),
                        result(resource(5, -49, 501), Math.sqrt(1.0)),
                        result(resource(5, -51, 501), Math.sqrt(1.0)),
                        result(resource(6, -49, 500), Math.sqrt(1.0)),
                        result(resource(6, -51, 500), Math.sqrt(1.0)),
                        result(resource(6, -50, 499), Math.sqrt(1.0)),
                        result(resource(6, -50, 501), Math.sqrt(1.0)),

                        result(resource(4, -49, 501), Math.sqrt(2.0)),
                        result(resource(4, -51, 501), Math.sqrt(2.0)),
                        result(resource(6, -49, 501), Math.sqrt(2.0)),
                        result(resource(6, -51, 501), Math.sqrt(2.0)),
                        result(resource(4, -49, 499), Math.sqrt(2.0)),
                        result(resource(4, -51, 499), Math.sqrt(2.0)),
                        result(resource(6, -49, 499), Math.sqrt(2.0)),
                        result(resource(6, -51, 499), Math.sqrt(2.0))
                );
    }

    @Override
    @Test
    public final void shouldOrderClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(5, -50, 500);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(27)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results.subList(0, 1))
                .extracting("score").isEqualTo(1.0);
        assertThat(results.subList(1, 7))
                .extracting("score").containsOnly(1.0);
        assertThat(results.subList(7, 19))
                .extracting("score").containsOnly(Math.sqrt(1.0));
        assertThat(results.subList(19, 27))
                .extracting("score").containsOnly(Math.sqrt(2.0));
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForALowestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        resource(-1000, -100, -10),
                        resource(-1000, -100, -9),
                        resource(-1000, -99, -10),
                        resource(-999, -100, -10)
                );
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        resource(10, 100, 1000),
                        resource(9, 100, 1000),
                        resource(10, 99, 1000),
                        resource(10, 100, 999)
                );
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAAllZeroesMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = getDataKeyClass();
        Vector<K> metadata = dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(7)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        resource(0, 0, 0),
                        resource(1, 0, 0),
                        resource(0, 1, 0),
                        resource(0, 0, 1),
                        resource(-1, 0, 0),
                        resource(0, -1, 0),
                        resource(0, 0, -1)
                );
    }

    private Vector<K> dataKey(Integer... values) {
        Vector<Integer> key = new Vector<>(Arrays.asList(values));
        return toDataKey(key);
    }

    private DataResource<K, D> resource(Integer... values) {
        Vector<K> key = dataKey(values);
        return DataResource.builder(getDataKeyClass(), getDataClass())
                .key(key)
                .data(toData(key))
                .build();
    }

    private DataFinderResult<K, D> result(DataResource<K, D> resource, Double score) {
        return DataFinderResult.builder(getDataKeyClass(), getDataClass())
                .resource(resource)
                .score(score)
                .build();
    }
}
