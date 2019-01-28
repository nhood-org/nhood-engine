package com.h8.nh.nhoodengine.core;

import java.util.Objects;
import java.util.Vector;

/**
 *
 * @param <K>
 * @param <D>
 */
public class DataResource<K, D> {

    /**
     *
     */
    private final Vector<K> key;

    /**
     *
     * @return
     */
    public Vector<K> getKey() {
        return key;
    }

    /**
     *
     */
    private final D data;

    /**
     *
     * @return
     */
    public D getData() {
        return data;
    }

    /**
     *
     * @param key
     * @param data
     */
    public DataResource(Vector<K> key, D data) {
        this.key = key;
        this.data = data;
    }

    /**
     *
     * @return
     */
    public static DataResourceBuilder builder() {
        return new DataResourceBuilder<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataResource<?, ?> that = (DataResource<?, ?>) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, data);
    }

    @Override
    public String toString() {
        return "DataResource{" +
                "key=" + key +
                ", data=" + data +
                '}';
    }

    /**
     *
     * @param <K>
     * @param <D>
     */
    public static final class DataResourceBuilder<K, D> {
        private Vector<K> key;
        private D data;

        private DataResourceBuilder() {
        }

        public DataResourceBuilder key(Vector<K> key) {
            this.key = key;
            return this;
        }

        public DataResourceBuilder data(D data) {
            this.data = data;
            return this;
        }

        public DataResource<K, D> build() {
            return new DataResource<>(key, data);
        }
    }
}
