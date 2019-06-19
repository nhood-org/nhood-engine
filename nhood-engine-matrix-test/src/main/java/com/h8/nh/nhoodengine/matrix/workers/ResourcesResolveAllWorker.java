package com.h8.nh.nhoodengine.matrix.workers;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;

import java.util.ArrayList;
import java.util.List;

public final class ResourcesResolveAllWorker<K extends DataResourceKey, D> implements Runnable {

    private final DataMatrixRepository<K, D> repository;

    private final K metadata;

    private final Integer expectedSize;

    private boolean hasErrors = false;

    private ResourcesResolveAllWorker(
            final DataMatrixRepository<K, D> repository,
            final K metadata,
            final Integer expectedSize) {
        this.repository = repository;
        this.metadata = metadata;
        this.expectedSize = expectedSize;
    }

    public static <K extends DataResourceKey, D> ResourcesResolveAllWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final K metadata) {
        return new ResourcesResolveAllWorker<>(repository, metadata, 0);
    }

    public static <K extends DataResourceKey, D> ResourcesResolveAllWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final K metadata,
            final Integer expectedSize) {
        return new ResourcesResolveAllWorker<>(repository, metadata, expectedSize);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        try {
            List<DataResource<K, D>> resources = resolveAllResources();
            System.out.println(Thread.currentThread().getName()
                    + " : Retrieved data of size: " + resources.size());
            hasErrors = resources.size() < expectedSize;
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()
                    + " : Could not resolve data from matrix repository because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());

            // Uncomment for troubleshooting purposes only
            // e.printStackTrace(System.err);

            hasErrors = true;
        }
    }

    private List<DataResource<K, D>> resolveAllResources()
            throws DataMatrixRepositoryFailedException {
        DataMatrixResourceIterator<K, D> iterator =
                repository.findNeighbours(metadata);

        List<DataResource<K, D>> retrievedResources = new ArrayList<>();
        while (iterator.hasNext()) {
            retrievedResources.addAll(iterator.next());
        }

        return retrievedResources;
    }
}
