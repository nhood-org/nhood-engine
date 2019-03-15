package com.h8.nh.nhoodengine.core;


import com.h8.nh.nhoodengine.utils.DataFinderTestContext;
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
 * an implementer has to implement a DataFinderTestContext interface.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
public abstract class DataFinderAbstractTest<K, D> implements DataFinderRequirements {

    private static final Vector<Integer> KEY_VECTOR_MIN_LIMIT = new Vector<>(Arrays.asList(-1000, -100, -10));

    private static final Vector<Integer> KEY_VECTOR_MAX_LIMIT = new Vector<>(Arrays.asList(10, 100, 1000));

    private DataFinderTestContext<K, D> ctx;

    private DataFinder<K, D> dataFinder;

    private List<DataResource<K, D>> data;

    /**
     * Creates a new instance of DataFinderTestContext which is ctx for the whole test suite.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinderTestContext.
     */
    protected abstract DataFinderTestContext<K, D> initializeContext();

    @BeforeEach
    public final void setUp() {
        ctx = initializeContext();
        dataFinder = ctx.initializeDataFinder();
        data = DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(ctx::dataKey)
                .map(k -> DataResource.builder(ctx.dataKeyClass(), ctx.dataClass())
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .collect(Collectors.toList());
        data.forEach(ctx::register);
    }

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
        Class<K> keyClass = ctx.dataKeyClass();
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
        Class<K> keyClass = ctx.dataKeyClass();
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(ctx.dataKey())
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0, 0);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0, 0);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0, 0);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0, 0);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(5, -50, 500);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(11)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(5, -50, 500),
                        ctx.resource(4, -50, 500),
                        ctx.resource(5, -49, 500),
                        ctx.resource(5, -51, 500),
                        ctx.resource(6, -50, 500),
                        ctx.resource(5, -50, 499),
                        ctx.resource(5, -50, 501),
                        ctx.resource(4, -49, 500),
                        ctx.resource(4, -51, 500),
                        ctx.resource(6, -49, 500),
                        ctx.resource(6, -51, 500)
                );
    }

    @Override
    @Test
    public final void shouldCalculateScoresOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(5, -50, 500);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(27)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .containsExactlyInAnyOrder(
                        ctx.result(ctx.resource(5, -50, 500), 0.0),

                        ctx.result(ctx.resource(4, -50, 500), 1.0),
                        ctx.result(ctx.resource(5, -49, 500), 1.0),
                        ctx.result(ctx.resource(5, -50, 499), 1.0),
                        ctx.result(ctx.resource(5, -50, 501), 1.0),
                        ctx.result(ctx.resource(5, -51, 500), 1.0),
                        ctx.result(ctx.resource(6, -50, 500), 1.0),

                        ctx.result(ctx.resource(4, -49, 500), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(4, -51, 500), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(4, -50, 499), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(4, -50, 501), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(5, -49, 499), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(5, -51, 499), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(5, -49, 501), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(5, -51, 501), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(6, -49, 500), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(6, -51, 500), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(6, -50, 499), Math.sqrt(1.0)),
                        ctx.result(ctx.resource(6, -50, 501), Math.sqrt(1.0)),

                        ctx.result(ctx.resource(4, -49, 501), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(4, -51, 501), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(6, -49, 501), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(6, -51, 501), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(4, -49, 499), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(4, -51, 499), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(6, -49, 499), Math.sqrt(2.0)),
                        ctx.result(ctx.resource(6, -51, 499), Math.sqrt(2.0))
                );
    }

    @Override
    @Test
    public final void shouldOrderClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(5, -50, 500);
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
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(-1000, -100, -10),
                        ctx.resource(-1000, -100, -9),
                        ctx.resource(-1000, -99, -10),
                        ctx.resource(-999, -100, -10)
                );
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(10, 100, 1000),
                        ctx.resource(9, 100, 1000),
                        ctx.resource(10, 99, 1000),
                        ctx.resource(10, 100, 999)
                );
    }

    @Override
    @Test
    public final void shouldReturnListOfClosestResultForAAllZeroesMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        Vector<K> metadata = ctx.dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(7)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(0, 0, 0),
                        ctx.resource(1, 0, 0),
                        ctx.resource(0, 1, 0),
                        ctx.resource(0, 0, 1),
                        ctx.resource(-1, 0, 0),
                        ctx.resource(0, -1, 0),
                        ctx.resource(0, 0, -1)
                );
    }
}
