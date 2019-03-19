package com.h8.nh.nhoodengine.core.impl;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderAbstractTest;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.utils.DataFinderTestContext;

import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

class DataScoreComputationEngineTest extends DataFinderAbstractTest<Integer, Object> {

    @Override
    protected DataFinderTestContext<Integer, Object> initializeContext() {

        Set<DataResource<Integer, Object>> container = ConcurrentHashMap.newKeySet();

        DataMatrixRepository<Integer, Object> repository = new DataMatrixRepository<Integer, Object>() {

            @Override
            public void add(DataResource<Integer, Object> resource) {
                container.add(resource);
            }

            @Override
            public List<DataResource<Integer, Object>> findClosest(Vector<Integer> metadata) {
                return container
                        .stream()
                        .map(r -> DataResource.builder(Integer.class, Object.class)
                                .key(r.getKey())
                                .data(r.getData())
                                .build())
                        .collect(Collectors.toList());
            }
        };

        DataScoreComputationEngine<Integer, Object> engine = DataScoreComputationEngineBuilder
                .engine(Integer.class, Object.class)
                .withKeyMapper(Integer::doubleValue)
                .withRepository(repository)
                .build();

        return new DataFinderTestContext<Integer, Object>() {

            @Override
            public DataFinder<Integer, Object> initializeDataFinder() {
                return engine;
            }

            @Override
            public void register(DataResource<Integer, Object> data) {
                try {
                    repository.add(data);
                } catch (DataMatrixRepositoryFailedException e) {
                    throw new IllegalStateException(
                            "Could not register data because of an exception", e);
                }
            }

            @Override
            public int registerDataSize() {
                return container.size();
            }

            @Override
            public Class<Integer> dataKeyClass() {
                return Integer.class;
            }

            @Override
            public Vector<Integer> dataKey(Vector<Integer> key) {
                return key;
            }

            @Override
            public Class<Object> dataClass() {
                return Object.class;
            }

            @Override
            public Object data(Vector<Integer> key) {
                return key.hashCode();
            }
        };
    }
}