package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataFinderKeyMapper;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public final class DataScoreComputationEngine<K, D> implements DataFinder<K, D> {

    private final DataFinderKeyMapper<K> keyMapper;

    private final DataMatrixRepository<K, D> repository;

    DataScoreComputationEngine(
            final DataFinderKeyMapper<K> keyMapper,
            final DataMatrixRepository<K, D> repository) {
        this.keyMapper = keyMapper;
        this.repository = repository;
    }

    @Override
    public List<DataFinderResult<K, D>> find(final DataFinderCriteria<K> criteria)
            throws DataFinderFailedException {
        if (criteria == null) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria may not be null");
        }

        if (criteria.getMetadata() == null) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata may not be null");
        }

        if (criteria.getMetadata().size() == 0) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata may not be empty");
        }

        if (criteria.getMetadata().size() != repository.getMetadataSize()) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria metadata size does not match data in repository");
        }

        if (criteria.getLimit() < 0) {
            throw new DataFinderFailedException(
                    "DataFinderCriteria limit may not be negative");
        }

        try {
            return repository.findCell(criteria.getMetadata())
                    .stream()
                    .map(r -> compute(r, criteria))
                    .sorted(Comparator.comparingDouble(DataFinderResult::getScore))
                    .limit(criteria.getLimit())
                    .collect(Collectors.toList());
        } catch (DataMatrixRepositoryFailedException e) {
            throw new DataFinderFailedException(
                    "Could not perform search operation due to an unexpected repository error", e);
        }
    }

    private DataFinderResult<K, D> compute(
            final DataResource<K, D> resource, final DataFinderCriteria<K> criteria) {
        double score = computeDistance(resource.getKey(), criteria.getMetadata());
        return new DataFinderResult<>(score, resource);
    }

    private double computeDistance(
            final Vector<K> resource, final Vector<K> metadata) {
        double distance = 0.0;
        for (int i = 0; i < resource.size(); i++) {
            double rPos = keyMapper.map(resource.get(i));
            double mPos = keyMapper.map(metadata.get(i));
            distance = distance + Math.pow(rPos - mPos, 2.0);
        }
        return Math.sqrt(distance);
    }
}
