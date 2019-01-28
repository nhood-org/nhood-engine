package com.h8.nh.nhoodengine.core;

import java.util.List;

/**
 *
 * @param <K>
 * @param <D>
 */
public interface DataFinder<K, D> {

    /**
     *
     * @param criteria
     * @return
     */
    List<DataFinderResult<K, D>> find(DataFinderCriteria<K> criteria);
}
