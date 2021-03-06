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
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public interface DataFinder<K extends DataResourceKey, D> {

    /**
     * Find a list of DataResource's in accordance with given criteria
     *
     * Data universe will be retrieved from data source in accordance
     * with criteria metadata vector. Then geometrical distances are computed.
     * Top n values (as the limit) will be returned in an order based
     * on a distance. The shorter distance the higher score and higher order.
     *
     * @param criteria a data finder criteria
     * @return list of data finder results's
     *
     * @throws DataFinderFailedException
     * when find operation cannot be performed
     */
    List<DataFinderResult<K, D>> find(DataFinderCriteria<K> criteria) throws DataFinderFailedException;
}
