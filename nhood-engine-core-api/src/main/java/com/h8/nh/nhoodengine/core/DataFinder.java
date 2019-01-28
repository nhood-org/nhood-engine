package com.h8.nh.nhoodengine.core;

import java.util.List;

public interface DataFinder<K, D> {

    List<DataResource<K, D>> find(DataFinderCriteria<K> criteria);
}
