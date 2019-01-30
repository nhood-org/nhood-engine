package com.h8.nh.nhoodengine.core;

import java.util.List;

/**
 * This interface is a main facade of core nhood engine functionality.
 *
 * Methods defined here cover DataResource search capabilities based on
 * a convenient and open criteria. Search results are wrapped with open
 * structure of search results so it is possible to enhance it with additional
 * statistics and metrics.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
public interface DataFinder<K, D> {

    /**
     * Find a list of DataResource's in accordance with given criteria
     * @param criteria a data finder criteria
     * @return list of data finder results's
     */
    List<DataFinderResult<K, D>> find(DataFinderCriteria<K> criteria);
}
