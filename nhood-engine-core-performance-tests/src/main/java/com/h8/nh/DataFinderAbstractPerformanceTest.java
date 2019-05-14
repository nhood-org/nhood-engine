package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.utils.DataFinderTestContext;
import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.util.List;

/**
 * DataFinderAbstractPerformanceTest is an abstract test class
 * that implements performance tests of DataFinder.
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
@State(Scope.Benchmark)
public abstract class DataFinderAbstractPerformanceTest<K extends DataResourceKey, D> {

    private static final int LIMIT = 50;

    @Param({"50", "100", "200", "500"})
    private int range;

    private DataFinderTestContext<K, D> ctx;

    private DataFinder<K, D> dataFinder;

    /**
     * Creates a new instance of DataFinderTestContext which is ctx for the whole test suite.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinderTestContext.
     */
    protected abstract DataFinderTestContext<K, D> initializeContext();

    @Setup(Level.Trial)
    public final void prepare() {
        System.out.println(
                "Initializing context");

        ctx = initializeContext();
        dataFinder = ctx.initializeDataFinder();

        Integer[] minLimit = createLimitVector(-1 * range);
        Integer[] maxLimit = createLimitVector(range);

        DataKeyGenerator
                .generate(minLimit, maxLimit)
                .map(ctx::dataKey)
                .map(k -> DataResource.builder(ctx.dataKeyClass(), ctx.dataClass())
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .forEach(ctx::register);

        System.out.println(
                "Initialized '" + Math.pow(range, minLimit.length) + "' data elements");
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void shouldReturnResultListOfRequestedSize()
            throws DataFinderFailedException {
        Class<K> keyClass = ctx.dataKeyClass();
        K metadata = ctx.dataKey(0, 0, 0);
        DataFinderCriteria<K> criteria = DataFinderCriteria.builder(keyClass)
                .metadata(metadata)
                .limit(LIMIT)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assert results.size() == LIMIT;
    }

    private Integer[] createLimitVector(final int range) {
        return new Integer[]{range / 2, range / 2, range / 2};
    }
}
