package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;

import java.util.List;
import java.util.Vector;

/**
 * This interface is a main facade of nhood engine data matrix
 * management functionality.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
public interface DataMatrixRepository<K, D> {

    /**
     * Add data resource into data matrix.
     *
     * This is a kind of data indexation in search engine terms.
     *
     * Data resource will be interpret and placed into section
     * of matrix corresponding to its metadata vector.
     *
     * @param resource a data resource to be indexed
     *
     * @throws DataMatrixRepositoryFailedException
     * when resource may not be added
     */
    void add(DataResource<K, D> resource)
            throws DataMatrixRepositoryFailedException;

    /**
     * Retrieval of list of closest data resources in accordance with given
     * metadata vector.
     *
     * Data resources will be retrieved form sections of matrix
     * surrounding a given metadata vector point.
     *
     * @param metadata metadata vector
     * @return a list of data resources retrieved
     *
     * @throws DataMatrixRepositoryFailedException
     * when find operation cannot be performed
     */
    List<DataResource<K, D>> findClosest(Vector<K> metadata)
            throws DataMatrixRepositoryFailedException;
}
