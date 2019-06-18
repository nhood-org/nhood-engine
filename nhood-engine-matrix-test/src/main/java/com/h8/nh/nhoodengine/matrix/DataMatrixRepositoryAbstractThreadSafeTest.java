package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.workers.ResourcesAddWorker;
import com.h8.nh.nhoodengine.matrix.workers.ResourcesResolveAllWorker;
import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataMatrixRepositoryAbstractThreadSafeTest is an abstract test class
 * that implements DataMatrixRepositoryThreadSafeRequirements.
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
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class DataMatrixRepositoryAbstractThreadSafeTest<K extends DataResourceKey, D>
        implements DataMatrixRepositoryThreadSafeRequirements {

    private static final Integer[] KEY_VECTOR_MIN_LIMIT = new Integer[]{-20, -20, -20};

    private static final Integer[] KEY_VECTOR_MAX_LIMIT = new Integer[]{20, 20, 20};

    private static final int RESOURCE_CHUNK_SIZE = 5000;

    private static final int THREAD_POOL_SIZE = 5;

    private static final int THREAD_POOL_TERMINATION_CHECK_INTERVAL_MILLISECONDS = 1000;

    private DataMatrixRepositoryTestContext<K, D> ctx = null;

    private DataMatrixRepository<K, D> dataMatrixRepository = null;

    /**
     * Creates a new instance of DataMatrixRepositoryTestContext which is ctx for the whole test suite.
     * This instance is initialized before each single test execution.
     *
     * @return an instance of DataMatrixRepositoryTestContext.
     */
    protected abstract DataMatrixRepositoryTestContext<K, D> initializeContext();

    @BeforeEach
    final void setUp() {
        if (ctx != null) {
            return;
        }
        ctx = initializeContext();
        dataMatrixRepository = ctx.initializerRepository();
    }

    @Override
    @Test
    public final void shouldNotLoseResourcesWhenThoseAreAddedConcurrently()
            throws DataMatrixRepositoryFailedException, InterruptedException {
        // given
        List<DataResource<K, D>> resources = generateResources();

        // when
        List<ResourcesAddWorker<K, D>> addResourceWorkers = generateAddResourceWorkers(resources);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        addResourceWorkers.forEach(executor::execute);
        shutdownExecutor(executor);

        // then
        assertThatResourceAddWorkersHaveNoErrors(addResourceWorkers);

        List<DataResource<K, D>> retrievedResources = resolveAllResources();
        assertThat(retrievedResources.size()).isEqualTo(resources.size());
    }

    @Override
    @Test
    public final void shouldNotLoseResourcesWhenThoseAreAddedAndResolvedConcurrently()
            throws DataMatrixRepositoryFailedException, InterruptedException {
        // given
        List<DataResource<K, D>> resources = generateResources();
        K key = ctx.dataKey(0, 0, 0);

        // when
        List<ResourcesAddWorker<K, D>> addResourceWorkers = generateAddResourceWorkers(resources);
        List<ResourcesResolveAllWorker<K, D>> resolveResourceWorkers = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        addResourceWorkers.forEach(w -> {
            ResourcesResolveAllWorker<K, D> resolveAllWorker =
                    ResourcesResolveAllWorker.of(dataMatrixRepository, key);
            resolveResourceWorkers.add(resolveAllWorker);
            executor.execute(resolveAllWorker);
            executor.execute(w);
        });

        shutdownExecutor(executor);

        // then
        assertThatResourceAddWorkersHaveNoErrors(addResourceWorkers);
        assertThatResourceResolveAllWorkersHaveNoErrors(resolveResourceWorkers);

        List<DataResource<K, D>> retrievedResources = resolveAllResources();
        assertThat(retrievedResources.size()).isEqualTo(resources.size());
    }

    @Override
    @Test
    public final void shouldResolveAllAlreadyAddedResources()
            throws InterruptedException {
        // given
        List<DataResource<K, D>> resources = generateResources();
        K key = ctx.dataKey(0, 0, 0);

        // when
        AtomicInteger resourceCounter = new AtomicInteger();

        List<ResourcesAddWorker<K, D>> addResourceWorkers = generateAddResourceWorkers(resources, resourceCounter);
        List<ResourcesResolveAllWorker<K, D>> resolveResourceWorkers = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        addResourceWorkers.forEach(w -> {
            ResourcesResolveAllWorker<K, D> resolveAllWorker =
                    ResourcesResolveAllWorker.of(dataMatrixRepository, key, resourceCounter.get());
            resolveResourceWorkers.add(resolveAllWorker);
            executor.execute(resolveAllWorker);
            executor.execute(w);
        });

        shutdownExecutor(executor);

        // then
        assertThatResourceAddWorkersHaveNoErrors(addResourceWorkers);
        assertThatResourceResolveAllWorkersHaveNoErrors(resolveResourceWorkers);
    }

    private List<DataResource<K, D>> generateResources() {
        return DataKeyGenerator
                .generate(KEY_VECTOR_MIN_LIMIT, KEY_VECTOR_MAX_LIMIT)
                .map(ctx::dataKey)
                .map(k -> DataResource.<K, D>builder()
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .collect(Collectors.toList());
    }

    private List<ResourcesAddWorker<K, D>> generateAddResourceWorkers(
            final List<DataResource<K, D>> resources) {
        AtomicInteger chunkCounter = new AtomicInteger();
        return resources.stream()
                .collect(groupingBy(x -> chunkCounter.getAndIncrement() / RESOURCE_CHUNK_SIZE)).values()
                .stream()
                .map(chunk -> ResourcesAddWorker.of(dataMatrixRepository, chunk))
                .collect(Collectors.toList());
    }

    private List<ResourcesAddWorker<K, D>> generateAddResourceWorkers(
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter) {
        AtomicInteger chunkCounter = new AtomicInteger();
        return resources.stream()
                .collect(groupingBy(x -> chunkCounter.getAndIncrement() / RESOURCE_CHUNK_SIZE)).values()
                .stream()
                .map(chunk -> ResourcesAddWorker.of(dataMatrixRepository, chunk, resourceCounter))
                .collect(Collectors.toList());
    }

    private void shutdownExecutor(final ExecutorService executor)
            throws InterruptedException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            System.out.println("Waiting for all workers to finish");
            Thread.sleep(THREAD_POOL_TERMINATION_CHECK_INTERVAL_MILLISECONDS);
        }
    }

    private List<DataResource<K, D>> resolveAllResources()
            throws DataMatrixRepositoryFailedException {
        K key = ctx.dataKey(0, 0, 0);

        DataMatrixResourceIterator<K, D> iterator =
                dataMatrixRepository.findNeighbours(key);

        List<DataResource<K, D>> retrievedResources = new ArrayList<>();
        while (iterator.hasNext()) {
            retrievedResources.addAll(iterator.next());
        }

        return retrievedResources;
    }

    private void assertThatResourceAddWorkersHaveNoErrors(
            final List<ResourcesAddWorker<K, D>> workers) {
        assertThat(
                workers.stream()
                        .map(ResourcesAddWorker::hasErrors)
                        .collect(Collectors.toList()))
                .containsOnly(false);
    }

    private void assertThatResourceResolveAllWorkersHaveNoErrors(
            final List<ResourcesResolveAllWorker<K, D>> workers) {
        assertThat(
                workers.stream()
                        .map(ResourcesResolveAllWorker::hasErrors)
                        .collect(Collectors.toList()))
                .containsOnly(false);
    }
}
