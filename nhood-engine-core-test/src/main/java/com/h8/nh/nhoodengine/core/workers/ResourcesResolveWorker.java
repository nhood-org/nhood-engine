package com.h8.nh.nhoodengine.core.workers;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.util.List;

public final class ResourcesResolveWorker<K extends DataResourceKey, D> implements Runnable {

    private final DataFinder<K, D> dataFinder;

    private final DataFinderCriteria<K> criteria;

    private boolean hasErrors = false;

    private ResourcesResolveWorker(
            final DataFinder<K, D> dataFinder,
            final DataFinderCriteria<K> criteria) {
        this.dataFinder = dataFinder;
        this.criteria = criteria;
    }

    public static <K extends DataResourceKey, D> ResourcesResolveWorker<K, D> of(
            final DataFinder<K, D> dataFinder,
            final DataFinderCriteria<K> criteria) {
        return new ResourcesResolveWorker<>(dataFinder, criteria);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public void run() {
        try {
            List<DataFinderResult<K, D>> results = dataFinder.find(criteria);
            System.out.println(Thread.currentThread().getName()
                    + " : Retrieved data of size: " + results.size());
            hasErrors = results.size() < criteria.getLimit();
        } catch (Exception e) {
            System.err.println(Thread.currentThread().getName()
                    + " : Could not find data because of"
                    + " an exception: " + e.getClass().getSimpleName() + " : " + e.getMessage());

            // Uncomment for troubleshooting purposes only
            // e.printStackTrace(System.err);

            hasErrors = true;
        }
    }
}
