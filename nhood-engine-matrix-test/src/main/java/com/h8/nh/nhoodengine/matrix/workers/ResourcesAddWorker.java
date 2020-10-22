package com.h8.nh.nhoodengine.matrix.workers;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class ResourcesAddWorker<K extends DataResourceKey, D> implements Runnable, AssertableWorker {

    private final DataMatrixRepository<K, D> repository;

    private final List<DataResource<K, D>> resources;

    private final AtomicInteger resourceCounter;

    private final SynchronousQueue<UUID> added;

    private boolean hasErrors = false;

    private ResourcesAddWorker(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter,
            final SynchronousQueue<UUID> added) {
        this.repository = repository;
        this.resources = resources;
        this.resourceCounter = resourceCounter;
        this.added = added;
    }

    public static <K extends DataResourceKey, D> ResourcesAddWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources) {
        return new ResourcesAddWorker<>(repository, resources, new AtomicInteger(), null);
    }

    public static <K extends DataResourceKey, D> ResourcesAddWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter) {
        return new ResourcesAddWorker<>(repository, resources, resourceCounter, null);
    }

    public static <K extends DataResourceKey, D> ResourcesAddWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final List<DataResource<K, D>> resources,
            final AtomicInteger resourceCounter,
            final SynchronousQueue<UUID> added) {
        return new ResourcesAddWorker<>(repository, resources, resourceCounter, added);
    }

    @Override
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
            System.err.println(Thread.currentThread().getName()
                    + " : Could not populate data matrix repository because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());
            hasErrors = true;
        }
    }

    private void populateResource(final DataResource<K, D> resource) {
        try {
            repository.add(resource);
            if (added != null) {
                added.put(resource.getUuid());
            }
        } catch (DataMatrixRepositoryFailedException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
