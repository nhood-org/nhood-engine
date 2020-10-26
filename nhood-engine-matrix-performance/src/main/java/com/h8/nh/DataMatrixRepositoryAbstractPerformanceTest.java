package com.h8.nh;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.*;
import com.h8.nh.nhoodengine.utils.measurement.node.ExecutionTimeMeasurement;
import com.h8.nh.nhoodengine.utils.measurement.node.HeapMemoryMeasurement;
import com.h8.nh.nhoodengine.utils.measurement.MeasurementChain;
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

    private static final int RESOURCE_DATA_POOL_SIZE = 5000;

    private static final int RESOURCE_METADATA_POOL_SIZE = RESOURCE_DATA_POOL_SIZE;

    private static final int RESOURCE_CRAWL_DEPTH = 1000;

    @Param({"10000", "100000", "1000000"})
    private int dataSetSize;

    @Param({"8", "16", "32"})
    private int metadataSize;

    private final SecureRandom random = new SecureRandom();

    private DataMatrixRepositoryTestContext<K, D> ctx = null;

    private DataMatrixRepository<K, D> dataMatrixRepository = null;

    private List<DataResource<K, D>> randomDataPool;

    private Integer[][] randomMetadataPool;

    private final List<DataResource<K, D>> generatedDataChunk = new ArrayList<>();

    private boolean revertRemovedDataChunk;

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
    public final void prepareTrial() {
        System.out.println(
                "Initializing context");
        System.out.println(
                "Resource loop size:        " + RESOURCE_DATA_POOL_SIZE);
        System.out.println(
                "Metadata loop size:        " + RESOURCE_METADATA_POOL_SIZE);
        System.out.println(
                "Resource crawl depth:      " + RESOURCE_CRAWL_DEPTH);

        ctx = initializeContext();
        dataMatrixRepository = ctx.initializerRepository();

        MeasurementChain.of("Data preparation", this::generateInitialRepositoryData)
                .measure(ExecutionTimeMeasurement.getInstance())
                .measure(HeapMemoryMeasurement.getInstance())
                .run();

        System.out.println(
                "Measurement::Data size:    " + dataSetSize);

        generateRandomDataPool();
        generateRandomMetadataPool();
    }

    @Setup(Level.Invocation)
    public final void prepareInvocation()
            throws DataMatrixRepositoryFailedException {
        if (revertRemovedDataChunk) {
            for (DataResource<K, D> r : generatedDataChunk) {
                dataMatrixRepository.add(r);
            }
        }
        revertRemovedDataChunk = false;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void addRandomResources()
            throws DataMatrixRepositoryFailedException {
        for (DataResource<K, D> r : randomDataPool) {
            dataMatrixRepository.add(r);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void removeRandomResources()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException {
        for (DataResource<K, D> r : generatedDataChunk) {
            dataMatrixRepository.remove(r.getUuid());
        }
        revertRemovedDataChunk = true;
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void findRandomResources()
            throws DataMatrixRepositoryFailedException, DataDoesNotExistException {
        for (DataResource<K, D> r : generatedDataChunk) {
            dataMatrixRepository.find(r.getUuid());
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void resolveNeighboursOfRandomMetadataKey()
            throws DataMatrixRepositoryFailedException {
        int idx = random.nextInt(RESOURCE_METADATA_POOL_SIZE);
        K metadata = ctx.dataKey(randomMetadataPool[idx]);
        DataMatrixResourceIterator<K, D> iterator = dataMatrixRepository.findNeighbours(metadata);
        assert iterator.hasNext();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public final void resolveAndCrawlNeighboursOfRandomMetadataKey()
            throws DataMatrixRepositoryFailedException {
        int idx = random.nextInt(RESOURCE_METADATA_POOL_SIZE);
        K metadata = ctx.dataKey(randomMetadataPool[idx]);
        DataMatrixResourceIterator<K, D> iterator = dataMatrixRepository.findNeighbours(metadata);
        for (int i = 0; i < RESOURCE_CRAWL_DEPTH; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            assert iterator.next() != null;
        }
    }

    private void generateInitialRepositoryData() {
        try {
            for (int i = 0; i < dataSetSize; i++) {
                DataResource<K, D> r = generateRandomData();
                dataMatrixRepository.add(r);
                if (i < RESOURCE_DATA_POOL_SIZE) {
                    generatedDataChunk.add(r);
                }
            }
        } catch (DataMatrixRepositoryFailedException e) {
            throw new IllegalStateException("Could not initialize data", e);
        }
    }

    private void generateRandomDataPool() {
        randomDataPool = new ArrayList<>();
        for (int i = 0; i < RESOURCE_DATA_POOL_SIZE; i++) {
            randomDataPool.add(generateRandomData());
        }
    }

    private void generateRandomMetadataPool() {
        randomMetadataPool = new Integer[RESOURCE_METADATA_POOL_SIZE][metadataSize];
        for (int i = 0; i < RESOURCE_METADATA_POOL_SIZE; i++) {
            randomMetadataPool[i] = generateRandomMetadata();
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
}
