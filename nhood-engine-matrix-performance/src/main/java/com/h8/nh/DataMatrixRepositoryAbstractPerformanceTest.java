package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryTestContext;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DataMatrixRepositoryAbstractPerformanceTest is an abstract test class
 * that implements performance tests of DataMatrixRepository.
 * <p>
 * This class has been abstracted in order to maintain generic approach.
 * <p>
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 * <p>
 * While testing a concrete implementation of DataMatrixRepository
 * an implementer has to implement a DataMatrixRepositoryTestContext interface.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
@State(Scope.Benchmark)
public abstract class DataMatrixRepositoryAbstractPerformanceTest<K extends DataResourceKey, D> {

    private static final int RESOURCE_CHUNK_SIZE = 1000;

    private static final int RESOURCE_CRAWL_DEPTH = 100;

    @Param({"10000", "100000", "1000000"})
    private int dataSetSize;

    @Param({"8", "16", "32"})
    private int metadataSize;

    private SecureRandom random = new SecureRandom();

    private DataMatrixRepositoryTestContext<K, D> ctx = null;

    private DataMatrixRepository<K, D> dataMatrixRepository = null;

    private List<DataResource<K, D>> testData;

    /**
     * Creates a new instance of DataMatrixRepositoryTestContext which is ctx for the whole test suite.
     * This instance is initialized before each single test execution.
     *
     * @return an instance of DataMatrixRepositoryTestContext.
     */
    protected abstract DataMatrixRepositoryTestContext<K, D> initializeContext();

    final int getDataSetSize() {
        return dataSetSize;
    }

    final int getMetadataSize() {
        return metadataSize;
    }

    @Setup(Level.Trial)
    public final void prepare()
            throws DataMatrixRepositoryFailedException {
        System.out.println(
                "Initializing context");

        ctx = initializeContext();
        dataMatrixRepository = ctx.initializerRepository();

        long memoryUsage = readUsedMemory();

        for (int i = 0; i < dataSetSize; i++) {
            dataMatrixRepository.add(generateRandomData());
        }

        memoryUsage = readUsedMemory() - memoryUsage;

        System.out.println(
                "Initialized " + dataSetSize + " data elements");
        System.out.println(
                "Initialized data of size: " + FileUtils.humanReadableByteCount(memoryUsage));

        testData = new ArrayList<>();
        for (int i = 0; i < RESOURCE_CHUNK_SIZE; i++) {
            testData.add(generateRandomData());
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void addRandomResources()
            throws DataMatrixRepositoryFailedException {
        for (DataResource<K, D> r : testData) {
            dataMatrixRepository.add(r);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void resolveNeighboursOfRandomMetadataKey()
            throws DataMatrixRepositoryFailedException {
        K metadata = ctx.dataKey(generateRandomMetadata());
        DataMatrixResourceIterator<K, D> iterator = dataMatrixRepository.findNeighbours(metadata);
        assert iterator.hasNext();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void resolveAndCrawlNeighboursOfRandomMetadataKey()
            throws DataMatrixRepositoryFailedException {
        K metadata = ctx.dataKey(generateRandomMetadata());
        DataMatrixResourceIterator<K, D> iterator = dataMatrixRepository.findNeighbours(metadata);
        for (int i = 0; i < RESOURCE_CRAWL_DEPTH; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            assert iterator.next() != null;
        }
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
