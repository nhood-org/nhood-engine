package com.h8.nh.nhoodengine.matrix.workers;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataDoesNotExistException;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public final class ResourcesFindWorker<K extends DataResourceKey, D> implements Runnable, AssertableWorker {

    private final DataMatrixRepository<K, D> repository;

    private final SynchronousQueue<UUID> toFind;

    private final Boolean notFoundExpected;

    private boolean hasErrors = false;

    private ResourcesFindWorker(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toFind,
            final Boolean notFoundExpected) {
        this.repository = repository;
        this.toFind = toFind;
        this.notFoundExpected = notFoundExpected;
    }

    public static <K extends DataResourceKey, D> ResourcesFindWorker<K, D> of(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toFind) {
        return new ResourcesFindWorker<>(repository, toFind, false);
    }

    public static <K extends DataResourceKey, D> ResourcesFindWorker<K, D> notFoundExpected(
            final DataMatrixRepository<K, D> repository,
            final SynchronousQueue<UUID> toFind) {
        return new ResourcesFindWorker<>(repository, toFind, true);
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        try {
            while (true) {
                UUID uuid = toFind.poll(200, TimeUnit.MILLISECONDS);
                if (uuid == null) {
                    return;
                }
                findResource(uuid);
            }
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()
                    + " : Could not remove data from matrix repository because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());
            hasErrors = true;
        }
    }

    private void findResource(final UUID uuid) {
        try {
            DataResource<K, D> resource = repository.find(uuid);
            if (resource == null) {
                throw new RuntimeException(
                        "Returned data is null");
            }
            if (resource.getUuid() != uuid) {
                throw new RuntimeException(
                        "Returned data has different UUID than requested");
            }
        } catch (DataDoesNotExistException e) {
            if (!notFoundExpected) {
                throw new RuntimeException(e);
            }
        } catch (DataMatrixRepositoryFailedException e) {
            throw new RuntimeException(e);
        }
    }
}
