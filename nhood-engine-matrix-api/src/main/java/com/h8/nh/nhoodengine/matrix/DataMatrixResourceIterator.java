package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;
import java.util.Set;

/**
 * This interface represents an iterator though all
 * {@link DataResource} elements returned by the repository.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public interface DataMatrixResourceIterator<K extends DataResourceKey, D> {

    /**
     * Return a chunk of resources found around the given entry point.
     * @return a next chunk of resources
     */
    Set<DataResource<K, D>> next();

    /**
     * Informs if there are more resources that include neighbours
     * of given entry point
     * @return there is another chunk of resources
     */
    boolean hasNext();

    /**
     * Informs if there are more resources that include neighbours
     * of given entry point within a given range.
     * @param range a radius of neighbour range.
     * @return there is another chunk of resources within a given radius
     */
    boolean hasNextWithinRange(BigDecimal range);
}
