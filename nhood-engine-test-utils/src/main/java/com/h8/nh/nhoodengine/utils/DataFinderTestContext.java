package com.h8.nh.nhoodengine.utils;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.DataResource;

import java.util.Arrays;
import java.util.Vector;

/**
 * DataFinderTestContext is an interface which defines
 * a couple of initialization and mapping utilities methods
 * used within an abstract test suites of DataFinder.
 *
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
public interface DataFinderTestContext<K, D> {

    /**
     * Creates a new instance of DataFinder which is a subject of testing.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinder.
     */
    DataFinder<K, D> initializeDataFinder();

    /**
     * Registers a data as a findable resource.
     * @param data data to be registered.
     */
    void register(DataResource<K, D> data);

    /**
     * Size of registered data.
     * @return a size of registered data.
     */
    int registerDataSize();

    /**
     * Key type
     * @return a generic type of data metadata key vector.
     */
    Class<K> dataKeyClass();

    /**
     * Maps a vector of integers into a vector of metadata of generic type K.
     * @param key a vector of integers.
     * @return a mapped vector.
     */
    Vector<K> dataKey(Vector<Integer> key);

    default Vector<K> dataKey(Integer... values) {
        Vector<Integer> key = new Vector<>(Arrays.asList(values));
        return dataKey(key);
    }

    /**
     * Data type
     * @return a generic type of data resource.
     */
    Class<D> dataClass();

    /**
     * Maps a vector of metadata of generic type K into a corresponding data.
     * @param key vector of metadata of generic type K.
     * @return a corresponding data.
     */
    D data(Vector<K> key);

    default D data(Integer... values) {
        Vector<Integer> key = new Vector<>(Arrays.asList(values));
        return data(dataKey(key));
    }

    default DataResource<K, D> resource(Integer... values) {
        Vector<K> key = dataKey(values);
        return DataResource.builder(dataKeyClass(), dataClass())
                .key(key)
                .data(data(key))
                .build();
    }

    default DataFinderResult<K, D> result(DataResource<K, D> resource, Double score) {
        return DataFinderResult.builder(dataKeyClass(), dataClass())
                .resource(resource)
                .score(score)
                .build();
    }
}
