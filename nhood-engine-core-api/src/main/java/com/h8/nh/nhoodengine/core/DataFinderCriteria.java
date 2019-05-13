package com.h8.nh.nhoodengine.core;

import java.util.Objects;

/**
 * This class is a set of data finder search criteria.
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 */
public final class DataFinderCriteria<K extends DataResourceKey> {

    /**
     * Metadata key vector representing a geometric point of interest
     */
    private final K metadata;

    /**
     * Limit of size of returned results
     */
    private final int limit;

    /**
     * Default constructor
     * @param metadata metadata key vector
     * @param limit size limit
     */
    public DataFinderCriteria(final K metadata, final int limit) {
        this.metadata = metadata;
        this.limit = limit;
    }

    /**
     * Metadata key vector representing a geometric point of interest
     * @return actual key vector value
     */
    public K getMetadata() {
        return metadata;
    }

    /**
     * Limit of size of returned results
     * @return actual size limit value
     */
    public int getLimit() {
        return limit;
    }

    /**
     * A static method exposing an auxiliary builder
     * @param keyClass key generic class
     * @return An instance of a builder
     */
    public static <K extends DataResourceKey> DataFinderCriteriaBuilder<K> builder(final Class<K> keyClass) {
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
     * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
     */
    public static final class DataFinderCriteriaBuilder<K extends DataResourceKey> {

        private K metadata;
        private int limit;

        private DataFinderCriteriaBuilder() {
        }

        /**
         * Metadata key vector representing a geometric point of interest
         * @param metadata key vector value
         * @return builder instance
         */
        public DataFinderCriteriaBuilder<K> metadata(final K metadata) {
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
