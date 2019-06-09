package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.core.utils.BoundedTreeSet;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_ROUNDING_MODE;
import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_SCALE;

/**
 * This is a default implementation {@link DataFinder} interface based on {@link DataMatrixRepository}.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public final class DataScoreComputationEngine<K extends DataResourceKey, D> implements DataFinder<K, D> {

    private final DataMatrixRepository<K, D> repository;

    public DataScoreComputationEngine(
            final DataMatrixRepository<K, D> repository) {
        this.repository = repository;
    }

    @Override
    public List<DataFinderResult<K, D>> find(final DataFinderCriteria<K> criteria)
            throws DataFinderFailedException {
        validate(criteria);

        if (criteria.getLimit() == 0) {
            return Collections.emptyList();
        }

        try {
            DataMatrixResourceIterator<K, D> iterator =
                    repository.findNeighbours(criteria.getMetadata());
            return find(criteria, iterator);
        } catch (DataMatrixRepositoryFailedException e) {
            throw new DataFinderFailedException(
                    "Could not find resources because of unexpected error", e);
        }
    }

    private List<DataFinderResult<K, D>> find(
            final DataFinderCriteria<K> criteria,
            final DataMatrixResourceIterator<K, D> iterator) {
        TreeSet<DataFinderResult<K, D>> sorted =
                new BoundedTreeSet<>(criteria.getLimit(), DataFinderResult::compareTo);

        do {
            Set<DataFinderResult<K, D>> neighbours =
                    iterator.next()
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

        if (criteria.getMetadata().unified().length != repository.getMetadataSize()) {
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
                .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);
    }

    private boolean shouldContinue(
            final DataMatrixResourceIterator<K, D> iterator,
            final TreeSet<DataFinderResult<K, D>> sorted,
            final DataFinderCriteria<K> criteria) {
        if (limitReached(sorted, criteria)) {
            return iterator.hasNextWithinRange(sorted.last().getScore());
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
