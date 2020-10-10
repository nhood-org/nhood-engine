package com.h8.nh.nhoodengine.core;

import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a basic data resource that is is being searched for
 * in the nhood engine.
 *
 * DataResource consists of three elements:
 * - uuid identifier
 * - a vector key that represents metadata of data resource
 * - a data itself
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public final class DataResource<K extends DataResourceKey, D>
        implements Comparable<DataResource<K, D>> {

    /**
     * A unique identifier
     */
    private final UUID uuid;

    /**
     * Data metadata key vector
     */
    private final K key;

    /**
     * Data resource
     */
    private final D data;

    /**
     * Default constructor with auto-generated uuid
     * @param key metadata key value
     * @param data data resource value
     */
    public DataResource(final K key, final D data) {
        this.uuid = UUID.randomUUID();
        this.key = key;
        this.data = data;
    }

    /**
     * Default constructor
     * @param uuid unique identifier
     * @param key metadata key value
     * @param data data resource value
     */
    public DataResource(final UUID uuid, final K key, final D data) {
        this.uuid = uuid;
        this.key = key;
        this.data = data;
    }

    /**
     * A unique identifier
     * @return actual unique identifier value
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Data metadata key vector
     * @return actual metadata key value
     */
    public K getKey() {
        return key;
    }

    /**
     * Data resource
     * @return actual data resource value
     */
    public D getData() {
        return data;
    }

    /**
     * A static method exposing an auxiliary builder
     *
     * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
     * @param <D> a generic type of data resource.
     * @return An instance of a builder
     */
    public static <K extends DataResourceKey, D> DataResourceBuilder<K, D> builder() {
        return new DataResourceBuilder<>();
    }

    @Override
    public int compareTo(final DataResource<K, D> o) {
        return this.uuid.compareTo(o.uuid);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataResource<?, ?> that = (DataResource<?, ?>) o;
        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "DataResource{"
                + "uuid=" + uuid
                + ", key=" + key
                + ", data=" + data
                + '}';
    }

    /**
     * An auxiliary builder of DataResource
     * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
     * @param <D> a generic type of data resource.
     */
    public static final class DataResourceBuilder<K extends DataResourceKey, D> {
        private K key;
        private D data;

        private DataResourceBuilder() {
        }

        /**
         * Data metadata key vector
         * @param key metadata key value
         * @return builder instance
         */
        public DataResourceBuilder<K, D> key(final K key) {
            this.key = key;
            return this;
        }

        /**
         * Data resource
         * @param data data resource value
         * @return builder instance
         */
        public DataResourceBuilder<K, D> data(final D data) {
            this.data = data;
            return this;
        }

        /**
         * Method will combine all gathered properties into DataResource instance
         * @return a build instance of DataResource
         */
        public DataResource<K, D> build() {
            return new DataResource<>(key, data);
        }
    }
}
