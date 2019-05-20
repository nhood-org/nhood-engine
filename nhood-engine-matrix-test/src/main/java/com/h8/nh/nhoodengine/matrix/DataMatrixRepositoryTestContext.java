package com.h8.nh.nhoodengine.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;

/**
 * DataMatrixRepositoryTestContext is an interface which defines
 * a couple of initialization and mapping utilities methods
 * used within an abstract test suites of DataMatrixRepository.
 * <p>
 * It is assumed that all sequences of metadata types may be mapped
 * into a sequence of integers and all relations, and geometrical features are inherited.
 * Therefore all tests are based on integer-typed vectors.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public interface DataMatrixRepositoryTestContext<K extends DataResourceKey, D> {

    /**
     * Creates a new instance of DataMatrixRepository which is a subject of testing.
     * This instance is initialized once before execution of all tests in the test class.
     *
     * @return an instance of DataMatrixRepository.
     */
    DataMatrixRepository<K, D> initializeResource();

    /**
     * Size of registered data.
     *
     * @return a size of registered data.
     */
    int registeredDataSize();

    /**
     * Key type
     *
     * @return a generic type of data metadata key vector.
     */
    Class<K> dataKeyClass();

    /**
     * Maps a vector of integers into a vector of metadata of generic type K.
     *
     * @param key a vector of integers.
     * @return a mapped vector.
     */
    K dataKey(K key);

    default K dataKey(Integer... values) {
        BigDecimal[] unified = new BigDecimal[values.length];
        for (int i = 0; i < values.length; i++) {
            unified[i] = BigDecimal.valueOf(values[i]);
        }
        DataResourceKey key = () -> unified;
        return dataKey((K) key);
    }

    /**
     * Data type
     *
     * @return a generic type of data resource.
     */
    Class<D> dataClass();

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
        return DataResource.builder(dataKeyClass(), dataClass())
                .key(key)
                .data(data(key))
                .build();
    }
}
