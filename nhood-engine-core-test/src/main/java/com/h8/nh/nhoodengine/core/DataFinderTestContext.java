package com.h8.nh.nhoodengine.core;

import java.math.BigDecimal;

/**
 * DataFinderTestContext is an interface which defines
 * a couple of initialization and mapping utilities methods
 * used within an abstract test suites of DataFinder.
 * <p>
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public interface DataFinderTestContext<K extends DataResourceKey, D> {

    /**
     * Creates a new instance of DataFinder which is a subject of testing.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataFinder.
     */
    DataFinder<K, D> initializeDataFinder();

    /**
     * Registers a data as a findable resource.
     *
     * @param data data to be registered.
     */
    void register(DataResource<K, D> data);

    /**
     * Return registered resource.
     *
     * @param key a metadata vector.
     * @return a registered resource.
     */
    DataResource<K, D> getResource(K key);

    /**
     * Size of registered data.
     *
     * @return a size of registered data.
     */
    int registeredDataSize();

    /**
     * Maps a vector of integers into a vector of metadata of generic type K.
     *
     * @param key a vector of integers.
     * @return a mapped vector.
     */
    K dataKey(K key);

    @SuppressWarnings("unchecked")
    default K dataKey(Integer... values) {
        BigDecimal[] unified = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++) {
            unified[i] = BigDecimal.valueOf(values[i]);
        }
        DataResourceKey key = () -> unified;
        return dataKey((K) key);
    }

    /**
     * Maps a vector of metadata of generic type K into a corresponding data.
     *
     * @param key vector of metadata of generic type K.
     * @return a corresponding data.
     */
    D data(K key);

    default D data(Integer... values) {
        return data(dataKey(values));
    }

    default DataResource<K, D> resource(Integer... values) {
        K key = dataKey(values);
        return getResource(key);
    }

    default DataFinderResult<K, D> result(DataResource<K, D> resource, BigDecimal score) {
        return DataFinderResult.<K, D>builder()
                .resource(resource)
                .score(score)
                .build();
    }
}
