package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;

import java.util.List;

public final class DataScoreComputationEngine<K, D> implements DataFinder<K, D> {

    @Override
    public List<DataFinderResult<K, D>> find(final DataFinderCriteria<K> criteria)
            throws DataFinderFailedException {
        return null;
    }
}
