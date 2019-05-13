package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.util.Set;
import java.util.Vector;

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
     * Retrieval of set of closest data resources in accordance with given
     * metadata vector.
     *
     * Data resources will be retrieved form a matrix cell
     * corresponding to a given metadata vector point.
     *
     * @param metadata metadata vector
     * @return a set of data resources retrieve
     *
     * @throws DataMatrixRepositoryFailedException
     * when find operation cannot be performed
     */
    Set<DataResource<K, D>> findCell(Vector<K> metadata)
            throws DataMatrixRepositoryFailedException;

    /**
     * Retrieval of set of data resources from surrounding cells
     * in accordance with given metadata vector.
     *
     * Data resources will be retrieved form matrix cells surrounding a cell
     * corresponding to a given metadata vector.
     *
     * @param metadata metadata vector
     * @return a set of data resources retrieved
     *
     * @throws DataMatrixRepositoryFailedException
     * when find operation cannot be performed
     */
    Set<DataResource<K, D>> findNeighbourCells(Vector<K> metadata)
            throws DataMatrixRepositoryFailedException;

    /**
     * Retrieval of set of data resources from surrounding cells
     * in accordance with given metadata vector and range.
     *
     * Data resources will be retrieved form matrix cells surrounding a cell
     * corresponding to a given metadata vector point within a given range diameter.
     *
     * @param metadata metadata vector
     * @param range a range diameter
     * @return a set of data resources retrieved
     *
     * @throws DataMatrixRepositoryFailedException
     * when find operation cannot be performed
     */
    Set<DataResource<K, D>> findNeighbourCells(Vector<K> metadata, Double range)
            throws DataMatrixRepositoryFailedException;
}
