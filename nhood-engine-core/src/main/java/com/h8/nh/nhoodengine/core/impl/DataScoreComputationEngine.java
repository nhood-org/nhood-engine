package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCell;
import com.h8.nh.nhoodengine.core.matrix.DataMatrixCellIterator;
import com.h8.nh.nhoodengine.core.utils.BoundedTreeSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public final class DataScoreComputationEngine<K extends DataResourceKey, D> implements DataFinder<K, D> {

    // TODO!!! move to configuration
    private static final int SCALE = 4;

    // TODO!!! remove
    private static final int SIZE = 3;

    private final DataMatrixCell<DataResource<K, D>> cell;

    DataScoreComputationEngine(
            final DataMatrixCell<DataResource<K, D>> cell) {
        this.cell = cell;
    }

    @Override
    public List<DataFinderResult<K, D>> find(final DataFinderCriteria<K> criteria)
            throws DataFinderFailedException {
        validate(criteria);

        if (criteria.getLimit() == 0) {
            return Collections.emptyList();
        }

        DataMatrixCellIterator<DataResource<K, D>> iterator =
                DataMatrixCellIterator.startWith(criteria.getMetadata().unified(), cell);

        TreeSet<DataFinderResult<K, D>> sorted =
                new BoundedTreeSet<>(criteria.getLimit(), DataFinderResult::compareTo);

        do {
            Set<DataFinderResult<K, D>> neighbours =
                    iterator.next().getResources()
                            .stream()
                            .map(r -> compute(r, criteria))
                            .collect(Collectors.toSet());
            sorted.addAll(neighbours);
        } while (shouldContinue(iterator, sorted, criteria));

        return sorted
                .stream()
                .limit(criteria.getLimit())
                .collect(Collectors.toList());
    }

    private void validate(final DataFinderCriteria<K> criteria)
            throws DataFinderFailedException {
        if (criteria == null) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria may not be null");
        }

        if (criteria.getMetadata() == null) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata may not be null");
        }

        if (criteria.getMetadata().unified().length == 0) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata may not be empty");
        }

        // TODO!!!
        if (criteria.getMetadata().unified().length != SIZE) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata size does not match data in repository");
        }

        if (criteria.getLimit() < 0) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria limit may not be negative");
        }
    }

    private DataFinderResult<K, D> compute(
            final DataResource<K, D> resource,
            final DataFinderCriteria<K> criteria) {
        BigDecimal score = computeDistance(
                resource.getKey().unified(),
                criteria.getMetadata().unified());
        return new DataFinderResult<>(score, resource);
    }

    private BigDecimal computeDistance(
            final BigDecimal[] resource,
            final BigDecimal[] metadata) {
        BigDecimal sumOfSquares = BigDecimal.ZERO;
        for (int i = 0; i < resource.length; i++) {
            BigDecimal d = resource[i].subtract(metadata[i]);
            sumOfSquares = sumOfSquares.add(d.pow(2));
        }
        return BigDecimal.valueOf(Math.sqrt(sumOfSquares.doubleValue()))
                .setScale(SCALE, RoundingMode.CEILING);
    }

    private boolean shouldContinue(
            final DataMatrixCellIterator<DataResource<K, D>> iterator,
            final TreeSet<DataFinderResult<K, D>> sorted,
            final DataFinderCriteria<K> criteria) {
        if (limitReached(sorted, criteria)) {
            return false;
//            return iterator.hasNextWithinRange(sorted.last().getScore());
        } else {
            return iterator.hasNext();
        }
    }

    private boolean limitReached(
            final TreeSet<DataFinderResult<K, D>> sorted,
            final DataFinderCriteria<K> criteria) {
        return sorted.size() >= criteria.getLimit();
    }
}
