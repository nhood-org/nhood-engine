package com.h8.nh.nhoodengine.core;


import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.List;

import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_ROUNDING_MODE;
import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_SCALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DataFinderAbstractTest is an abstract test class
 * that implements DataFinderRequirements.
 * <p>
 * This class has been abstracted in order to maintain generic approach.
 * <p>
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 * <p>
 * While testing a concrete implementation of DataFinder
 * an implementer has to implement a DataFinderTestContext interface.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class DataFinderAbstractTest<K extends DataResourceKey, D> implements DataFinderRequirements {

    private static final BigDecimal DISTANCE_ZERO = BigDecimal.ZERO
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final BigDecimal DISTANCE_ONE = BigDecimal.ONE
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final double DIAGONAL_SQUARE = Math.sqrt(2.0);
    private static final BigDecimal DISTANCE_DIAGONAL_SQUARE = BigDecimal.valueOf(DIAGONAL_SQUARE)
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final double DIAGONAL_CUBE = Math.sqrt(3.0);
    private static final BigDecimal DISTANCE_DIAGONAL_CUBE = BigDecimal.valueOf(DIAGONAL_CUBE)
            .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);

    private static final Integer[] KEY_VECTOR_MIN_LIMIT = new Integer[]{-100, -100, -10};

    private static final Integer[] KEY_VECTOR_MAX_LIMIT = new Integer[]{10, 100, 100};

    private DataFinderTestContext<K, D> ctx;

    private DataFinder<K, D> dataFinder;

    /**
     * Creates a new instance of DataFinderTestContext which is ctx for the whole test suite.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinderTestContext.
     */
    protected abstract DataFinderTestContext<K, D> initializeContext();

    @BeforeEach
    final void setUp() {
        if (ctx != null) {
            return;
        }
        ctx = initializeContext();
        dataFinder = ctx.initializeDataFinder();
        DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(ctx::dataKey)
                .map(k -> DataResource.builder(ctx.dataKeyClass(), ctx.dataClass())
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .forEach(ctx::register);
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
    @SuppressWarnings("checkstyle:magicnumber")
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
    @SuppressWarnings("checkstyle:magicnumber")
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
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldThrowAnExceptionWhenCriteriaMetadataVectorSizeDoesNotMatch() {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0);
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
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldThrowAnExceptionWhenCriteriaLimitIsNegative() {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
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
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnAnEmptyResultListWhenCriteriaLimitZero()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(0)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results).isEmpty();
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnResultListOfLimitSizeWhenCriteriaLimitIsBelowDataSetSize()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(10)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results).hasSize(criteria.getLimit());
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnWholeResultSetWhenCriteriaLimitHigherThanDataSetSize()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(Integer.MAX_VALUE)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results).hasSize(ctx.registeredDataSize());
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnListOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(5, -50, 50);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(7)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(5, -50, 50),
                        ctx.resource(4, -50, 50),
                        ctx.resource(5, -49, 50),
                        ctx.resource(5, -50, 49),
                        ctx.resource(5, -50, 51),
                        ctx.resource(5, -51, 50),
                        ctx.resource(6, -50, 50)
                );
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldCalculateScoresOfClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(5, -50, 50);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(27)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .containsExactlyInAnyOrder(
                        ctx.result(ctx.resource(5, -50, 50), DISTANCE_ZERO),

                        ctx.result(ctx.resource(4, -50, 50), DISTANCE_ONE),
                        ctx.result(ctx.resource(5, -49, 50), DISTANCE_ONE),
                        ctx.result(ctx.resource(5, -50, 49), DISTANCE_ONE),
                        ctx.result(ctx.resource(5, -50, 51), DISTANCE_ONE),
                        ctx.result(ctx.resource(5, -51, 50), DISTANCE_ONE),
                        ctx.result(ctx.resource(6, -50, 50), DISTANCE_ONE),

                        ctx.result(ctx.resource(4, -49, 50), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(4, -51, 50), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(4, -50, 49), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(4, -50, 51), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(5, -49, 49), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(5, -51, 49), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(5, -49, 51), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(5, -51, 51), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(6, -49, 50), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(6, -51, 50), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(6, -50, 49), DISTANCE_DIAGONAL_SQUARE),
                        ctx.result(ctx.resource(6, -50, 51), DISTANCE_DIAGONAL_SQUARE),

                        ctx.result(ctx.resource(4, -49, 51), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(4, -51, 51), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(6, -49, 51), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(6, -51, 51), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(4, -49, 49), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(4, -51, 49), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(6, -49, 49), DISTANCE_DIAGONAL_CUBE),
                        ctx.result(ctx.resource(6, -51, 49), DISTANCE_DIAGONAL_CUBE)
                );
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldOrderClosestResultForAGivenMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(5, -50, 50);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(27)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results.subList(0, 1))
                .extracting("score").containsOnly(DISTANCE_ZERO);
        assertThat(results.subList(1, 7))
                .extracting("score").containsOnly(DISTANCE_ONE);
        assertThat(results.subList(7, 19))
                .extracting("score").containsOnly(DISTANCE_DIAGONAL_SQUARE);
        assertThat(results.subList(19, 27))
                .extracting("score").containsOnly(DISTANCE_DIAGONAL_CUBE);
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnListOfClosestResultForALowestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(-100, -100, -10),
                        ctx.resource(-100, -100, -9),
                        ctx.resource(-100, -99, -10),
                        ctx.resource(-99, -100, -10)
                );
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnListOfClosestResultForAHighestPossibleMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(4)
                .build();

        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);

        assertThat(results)
                .extracting("resource")
                .containsExactlyInAnyOrder(
                        ctx.resource(9, 99, 99),
                        ctx.resource(8, 99, 99),
                        ctx.resource(9, 98, 99),
                        ctx.resource(9, 99, 98)
                );
    }

    @Override
    @Test
    @SuppressWarnings("checkstyle:magicnumber")
    public final void shouldReturnListOfClosestResultForAllZeroesMetadataVector()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
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
