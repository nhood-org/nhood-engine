package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinderKeyMapper;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;

public final class DataScoreComputationEngineBuilder<K, D>  {

    private DataFinderKeyMapper<K> keyMapper;
    private DataMatrixRepository<K, D> repository;

    private DataScoreComputationEngineBuilder() {
    }

    public static <K, D> DataScoreComputationEngineBuilder<K, D> engine(
            final Class<K> keyClass, final Class<D> dataClass) {
        return new DataScoreComputationEngineBuilder<>();
    }

    public DataScoreComputationEngineBuilder<K, D> withKeyMapper(
            final DataFinderKeyMapper<K> keyMapper) {
        this.keyMapper = keyMapper;
        return this;
    }

    public DataScoreComputationEngineBuilder<K, D> withRepository(
            final DataMatrixRepository<K, D> repository) {
        this.repository = repository;
        return this;
    }

    public DataScoreComputationEngine<K, D> build() {
        return new DataScoreComputationEngine<>(keyMapper, repository);
    }
}
