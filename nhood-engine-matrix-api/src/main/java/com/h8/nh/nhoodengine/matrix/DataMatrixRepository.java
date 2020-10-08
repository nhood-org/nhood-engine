package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.util.UUID;

/**
 * This interface is a main facade of nhood engine data matrix
 * management functionality.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public interface DataMatrixRepository<K extends DataResourceKey, D> {

    /**
     * Returns size of metadata vector accepted within repository
     * @return size of metadata vector
     */
    int getMetadataSize();

    /**
     * Add data resource into data matrix.
     *
     * @param resource a data resource to be indexed
     *
     * @throws DataMatrixRepositoryFailedException
     * when resource may not be added
     */
    void add(DataResource<K, D> resource)
            throws DataMatrixRepositoryFailedException;

    /**
     * Find and return data resource from data matrix.
     *
     * @param uuid resource identifier
     *
     * @throws DataDoesNotExistException
     * when requested resource does not exist
     * @throws DataMatrixRepositoryFailedException
     * when resource could not be found because of an error
     */
    DataResource<K, D>  find(UUID uuid)
            throws DataDoesNotExistException, DataMatrixRepositoryFailedException;

    /**
     * Find and delete data resource from data matrix.
     *
     * @param uuid resource identifier
     *
     * @throws DataDoesNotExistException
     * when requested resource does not exist
     * @throws DataMatrixRepositoryFailedException
     * when resource could not be deleted because of an error
     */
    DataResource<K, D>  delete(UUID uuid)
            throws DataDoesNotExistException, DataMatrixRepositoryFailedException;

    /**
     * Returns an iterator of data resources closes to the given metadata vector.
     *
     * @param metadata metadata vector
     * @return an iterator of data resource chunks
     *
     * @throws DataMatrixRepositoryFailedException
     * when find operation cannot be performed
     */
    DataMatrixResourceIterator<K, D> findNeighbours(K metadata)
            throws DataMatrixRepositoryFailedException;
}
