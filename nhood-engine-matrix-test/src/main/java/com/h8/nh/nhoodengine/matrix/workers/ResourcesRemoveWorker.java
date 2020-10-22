package com.h8.nh.nhoodengine.matrix.workers;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataDoesNotExistException;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public final class ResourcesRemoveWorker<K extends DataResourceKey, D> implements Runnable, AssertableWorker {

    private final DataMatrixRepository<K, D> repository;

    private final SynchronousQueue<UUID> toRemove;

    private final SynchronousQueue<UUID> removed;

    private boolean hasErrors = false;

    private ResourcesRemoveWorker(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toRemove,
            final SynchronousQueue<UUID> removed) {
        this.repository = repository;
        this.toRemove = toRemove;
        this.removed = removed;
    }

    public static <K extends DataResourceKey, D> ResourcesRemoveWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toRemove) {
        return new ResourcesRemoveWorker<>(repository, toRemove, null);
    }

    public static <K extends DataResourceKey, D> ResourcesRemoveWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toRemove,
            final SynchronousQueue<UUID> removed) {
        return new ResourcesRemoveWorker<>(repository, toRemove, removed);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        try {
            while (true) {
                UUID uuid = toRemove.poll(100, TimeUnit.MILLISECONDS);
                if (uuid == null) {
                    return;
                }
                removeResource(uuid);
            }
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()
                    + " : Could not remove data from matrix repository because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());
            hasErrors = true;
        }
    }

    private void removeResource(final UUID uuid) {
        try {
            DataResource<K, D> resource = repository.remove(uuid);
            if (resource == null) {
                throw new RuntimeException(
                        "Returned data is null");
            }
            if (resource.getUuid() != uuid) {
                throw new RuntimeException(
                        "Returned data has different UUID than requested");
            }
            if (removed != null) {
                removed.put(uuid);
            }
        } catch (DataMatrixRepositoryFailedException | DataDoesNotExistException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
