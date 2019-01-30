package com.h8.nh.nhoodengine.core;

import java.util.Objects;

/**
 * This class is a wrapper of data resource returned by data fined.
 * Data is enriched with additional stats and metrics.
 *
 * @param <K> a generic type of data metadata key vector.
 * @param <D> a generic type of data resource.
 */
public final class DataFinderResult<K, D> {

    /**
     * A score which resource gained during the course of evaluation
     */
    private final double score;

    /**
     * A score which resource gained during the course of evaluation
     * @return actual score value
     */
    public double getScore() {
        return score;
    }

    /**
     * An actual data resource found
     */
    private final DataResource<K, D> resource;

    /**
     * An actual data resource found
     * @return actual resource value
     */
    public DataResource<K, D> getResource() {
        return resource;
    }

    /**
     * Default constructor
     * @param score score value
     * @param resource resource value
     */
    public DataFinderResult(final double score, final DataResource<K, D> resource) {
        this.score = score;
        this.resource = resource;
    }

    /**
     * A static method exposing an auxiliary builder
     * @return An instance of a builder
     */
    public static DataFinderResultBuilder builder() {
        return new DataFinderResultBuilder<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataFinderResult<?, ?> that = (DataFinderResult<?, ?>) o;
        return Double.compare(that.score, score) == 0
                && Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, resource);
    }

    @Override
    public String toString() {
        return "DataFinderResult{"
                + "score=" + score
                + ", resource=" + resource
                + '}';
    }

    /**
     * An auxiliary builder of DataFinderResult
     * @param <K> a generic type of data metadata key vector.
     * @param <D> a generic type of data resource.
     */
    public static final class DataFinderResultBuilder<K, D> {
        private double score;
        private DataResource<K, D> resource;

        private DataFinderResultBuilder() {
        }

        /**
         * A score which resource gained during the course of evaluation
         * @param score score value
         * @return builder instance
         */
        public DataFinderResultBuilder score(final double score) {
            this.score = score;
            return this;
        }

        /**
         * An actual data resource found
         * @param resource resource value
         * @return builder instance
         */
        public DataFinderResultBuilder resource(final DataResource<K, D> resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Method will combine all gathered properties into DataFinderResult instance
         * @return a build instance of DataFinderResult
         */
        public DataFinderResult build() {
            return new DataFinderResult<>(score, resource);
        }
    }
}
