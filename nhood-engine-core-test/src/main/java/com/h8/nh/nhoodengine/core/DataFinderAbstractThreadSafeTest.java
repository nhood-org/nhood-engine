package com.h8.nh.nhoodengine.core;

import com.h8.nh.nhoodengine.core.workers.ResourcesRandomAddWorker;
import com.h8.nh.nhoodengine.core.workers.ResourcesResolveWorker;
import com.h8.nh.nhoodengine.utils.DataKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataFinderAbstractThreadSafeTest is an abstract test class
 * that implements DataFinderThreadSafeRequirements.
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
public abstract class DataFinderAbstractThreadSafeTest<K extends DataResourceKey, D>
        implements DataFinderThreadSafeRequirements {

    private static final Integer[] KEY_VECTOR_MIN_LIMIT = new Integer[]{-100, -100, -10};

    private static final Integer[] KEY_VECTOR_MAX_LIMIT = new Integer[]{10, 100, 100};

    private static final int CONCURRENT_SEARCHES_SIZE = 100;

    private static final int CONCURRENT_SEARCHES_LIMIT = 30;

    private static final int THREAD_POOL_SIZE = 5;

    private static final int THREAD_POOL_TERMINATION_CHECK_INTERVAL_MILLISECONDS = 1000;

    private DataFinderTestContext<K, D> ctx = null;

    private DataFinder<K, D> dataFinder = null;

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
                .map(k -> DataResource.<K, D>builder()
                        .key(k)
                        .data(ctx.data(k))
                        .build())
                .forEach(ctx::register);
    }

    @Override
    @Test
    public void shouldHandleMultipleConcurrentSearches()
            throws InterruptedException {
        // given
        SecureRandom random = new SecureRandom();
        List<ResourcesResolveWorker<K, D>> resolveResourceWorkers = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_SEARCHES_SIZE; i++) {
            K metadata = ctx.dataKey(random.nextInt(), random.nextInt(), random.nextInt());
            DataFinderCriteria<K> criteria = DataFinderCriteria.<K>builder()
                    .metadata(metadata)
                    .limit(CONCURRENT_SEARCHES_LIMIT)
                    .build();
            resolveResourceWorkers.add(ResourcesResolveWorker.of(dataFinder, criteria));
        }

        // when
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        resolveResourceWorkers.forEach(executor::execute);
        shutdownExecutor(executor);

        // then
        assertThatResourceResolveWorkersHaveNoErrors(resolveResourceWorkers);
    }

    @Override
    @Test
    public void shouldHandleMultipleConcurrentSearchesAndDataGrowth()
            throws InterruptedException {
        // given
        SecureRandom random = new SecureRandom();
        List<ResourcesResolveWorker<K, D>> resolveResourceWorkers = new ArrayList<>();
        List<ResourcesRandomAddWorker<K, D>> addRandomResourceWorkers = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_SEARCHES_SIZE; i++) {
            K metadata = ctx.dataKey(random.nextInt(), random.nextInt(), random.nextInt());
            DataFinderCriteria<K> criteria = DataFinderCriteria.<K>builder()
                    .metadata(metadata)
                    .limit(CONCURRENT_SEARCHES_LIMIT)
                    .build();
            resolveResourceWorkers.add(ResourcesResolveWorker.of(dataFinder, criteria));
        }

        // when
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        resolveResourceWorkers.forEach(w -> {
            ResourcesRandomAddWorker<K, D> addRandomWorker = ResourcesRandomAddWorker.of(ctx);
            addRandomResourceWorkers.add(addRandomWorker);
            executor.execute(addRandomWorker);
            executor.execute(w);
        });
        shutdownExecutor(executor);

        // then
        assertThatResourceResolveWorkersHaveNoErrors(resolveResourceWorkers);
        assertThatResourceRandomAddWorkersHaveNoErrors(addRandomResourceWorkers);
    }

    private void shutdownExecutor(final ExecutorService executor)
            throws InterruptedException {
        executor.shutdown();
        while (!executor.isTerminated()) {
            System.out.println("Waiting for all workers to finish");
            Thread.sleep(THREAD_POOL_TERMINATION_CHECK_INTERVAL_MILLISECONDS);
        }
    }

    private void assertThatResourceResolveWorkersHaveNoErrors(
            final List<ResourcesResolveWorker<K, D>> workers) {
        assertThat(
                workers.stream()
                        .map(ResourcesResolveWorker::hasErrors)
                        .collect(Collectors.toList()))
                .containsOnly(false);
    }

    private void assertThatResourceRandomAddWorkersHaveNoErrors(
            final List<ResourcesRandomAddWorker<K, D>> workers) {
        assertThat(
                workers.stream()
                        .map(ResourcesRandomAddWorker::hasErrors)
                        .collect(Collectors.toList()))
                .containsOnly(false);
    }
}
