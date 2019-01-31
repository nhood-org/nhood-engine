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
     */
    void add(DataResource<K, D> resource);

    /**
     * Retrieval of list of data resource in accordance with given
     * facade defined as vector position and value.
     *
     * Data resources will be retrieved form section of matrix
     * corresponding metadata position and value.
     *
     * @param index metadata vector index
     * @param value metadata vector value
     * @return a list of data resources retrieved
     */
    List<DataResource> findFacade(int index, K value);

    /**
     * Retrieval of list of data resource in accordance with given
     * metadata vector.
     *
     * Data resources will be retrieved form sections of matrix
     * corresponding to all metadata vector values and merged.
     * A logical intersection of all lists is returned.
     *
     * @param metadata metadata vector
     * @return a list of data resources retrieved
     */
    List<DataResource> findAll(Vector<K> metadata);
}
