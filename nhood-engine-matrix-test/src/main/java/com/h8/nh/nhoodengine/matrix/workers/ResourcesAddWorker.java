package com.h8.nh.nhoodengine.matrix.workers;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class ResourcesAddWorker<K extends DataResourceKey, D> implements Runnable {

    private final DataMatrixRepository<K, D> repository;

    private final List<DataResource<K, D>> resources;

    private final AtomicInteger resourceCounter;

    private boolean hasErrors = false;

    private ResourcesAddWorker(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter) {
        this.repository = repository;
        this.resources = resources;
        this.resourceCounter = resourceCounter;
    }

    public static <K extends DataResourceKey, D> ResourcesAddWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources) {
        return new ResourcesAddWorker<>(repository, resources, new AtomicInteger());
    }

    public static <K extends DataResourceKey, D> ResourcesAddWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter) {
        return new ResourcesAddWorker<>(repository, resources, resourceCounter);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()
                + " : Populating data chunk of size: " + resources.size());
        try {
            resources.forEach(this::populateResource);
            resourceCounter.addAndGet(resources.size());
        } catch (Exception e) {
            System.out.println(Thread.currentThread().getName()
                    + " : Could not populate data matrix repository because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());

            // Uncomment for troubleshooting purposes only
            // e.printStackTrace(System.err);

            hasErrors = true;
        }
    }

    private void populateResource(final DataResource<K, D> resource) {
        try {
            repository.add(resource);
        } catch (DataMatrixRepositoryFailedException e) {
            throw new RuntimeException(
                    " Could not initialize data matrix repository because an exception", e);
        }
    }
}
