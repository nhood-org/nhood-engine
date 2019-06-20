package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataFinderTestContext;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.utils.FileUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

import java.security.SecureRandom;
import java.util.Arrays;
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

    @Param({"10000", "100000", "1000000"})
    private int dataSetSize;

    @Param({"8", "16", "32"})
    private int metadataSize;

    private SecureRandom random = new SecureRandom();

    private DataFinderTestContext<K, D> ctx;

    private DataFinder<K, D> dataFinder;

    /**
     * Creates a new instance of DataFinderTestContext which is ctx for the whole test suite.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinderTestContext.
     */
    protected abstract DataFinderTestContext<K, D> initializeContext();

    final int getDataSetSize() {
        return dataSetSize;
    }

    final int getMetadataSize() {
        return metadataSize;
    }

    @Setup(Level.Trial)
    public final void prepare() {
        System.out.println(
                "Initializing context");

        ctx = initializeContext();
        dataFinder = ctx.initializeDataFinder();

        long memoryUsage = readUsedMemory();

        for (int i = 0; i < dataSetSize; i++) {
            ctx.register(generateRandomData());
        }

        memoryUsage = readUsedMemory() - memoryUsage;

        System.out.println(
                "Initialized " + ctx.registeredDataSize() + " data elements");
        System.out.println(
                "Initialized data of size: " + FileUtils.humanReadableByteCount(memoryUsage));
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void shouldFindDataRelevantToRandomMetadataKey()
            throws DataFinderFailedException {
        K metadata = ctx.dataKey(generateRandomMetadata());
        DataFinderCriteria<K> criteria = DataFinderCriteria.<K>builder()
                .metadata(metadata)
                .limit(LIMIT)
                .build();
        List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
        assert results.size() == LIMIT;
    }

    private DataResource<K, D> generateRandomData() {
        K metadata = ctx.dataKey(generateRandomMetadata());
        return DataResource.<K, D>builder()
                .key(metadata)
                .data(ctx.data(metadata))
                .build();
    }

    private Integer[] generateRandomMetadata() {
        Integer[] v = new Integer[metadataSize];
        Arrays.fill(v, generateRandomInteger());
        return v;
    }

    private Integer generateRandomInteger() {
        return random.nextInt(Integer.MAX_VALUE) - (Integer.MAX_VALUE / 2);
    }

    private long readUsedMemory() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
