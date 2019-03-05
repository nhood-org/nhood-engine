package com.h8.nh.nhoodengine.core;

import java.util.Objects;
import java.util.Vector;

/**
 * This class is a set of data finder search criteria.
 *
 * @param <K> a generic type of data metadata key vector.
 */
public final class DataFinderCriteria<K> {

    /**
     * Metadata key vector representing a geometric point of interest
     */
    private final Vector<K> metadata;

    /**
     * Metadata key vector representing a geometric point of interest
     * @return actual key vector value
     */
    public Vector<K> getMetadata() {
        return metadata;
    }

    /**
     * Limit of size of returned results
     */
    private final int limit;

    /**
     * Limit of size of returned results
     * @return actual size limit value
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Default constructor
     * @param metadata metadata key vector
     * @param limit size limit
     */
    public DataFinderCriteria(final Vector<K> metadata, final int limit) {
        this.metadata = metadata;
        this.limit = limit;
    }

    /**
     * A static method exposing an auxiliary builder
     * @param c DataFinderCriteria generic class
     * @return An instance of a builder
     */
    public static <K> DataFinderCriteriaBuilder<K> builder(final Class<K> c) {
        return new DataFinderCriteriaBuilder<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataFinderCriteria<?> that = (DataFinderCriteria<?>) o;
        return limit == that.limit
                && Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, limit);
    }

    @Override
    public String toString() {
        return "DataFinderCriteria{"
                + "metadata=" + metadata
                + ", limit=" + limit
                + '}';
    }

    /**
     * An auxiliary builder of DataFinderCriteria
     * @param <K> a generic type of data metadata key vector.
     */
    public static final class DataFinderCriteriaBuilder<K> {

        private Vector<K> metadata;
        private int limit;

        private DataFinderCriteriaBuilder() {
        }

        /**
         * Metadata key vector representing a geometric point of interest
         * @param metadata key vector value
         * @return builder instance
         */
        public DataFinderCriteriaBuilder<K> metadata(final Vector<K> metadata) {
            this.metadata = metadata;
            return this;
        }

        /**
         * Limit of size of returned results
         * @param limit size limit value
         * @return builder instance
         */
        public DataFinderCriteriaBuilder<K> limit(final int limit) {
            this.limit = limit;
            return this;
        }

        /**
         * Method will combine all gathered properties into DataFinderCriteria instance
         * @return a build instance of DataFinderCriteria
         */
        public DataFinderCriteria<K> build() {
            return new DataFinderCriteria<>(metadata, limit);
        }
    }
}
