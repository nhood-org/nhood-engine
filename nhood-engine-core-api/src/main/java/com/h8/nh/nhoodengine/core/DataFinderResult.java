package com.h8.nh.nhoodengine.core;

import java.util.Objects;

/**
 *
 */
public class DataFinderResult<K, D> {

    /**
     *
     */
    private final double score;

    /**
     *
     * @return
     */
    public double getScore() {
        return score;
    }

    /**
     *
     */
    private final DataResource<K, D> resource;

    /**
     *
     * @return
     */
    public DataResource<K, D> getResource() {
        return resource;
    }

    /**
     *
     * @param score
     * @param resource
     */
    public DataFinderResult(double score, DataResource<K, D> resource) {
        this.score = score;
        this.resource = resource;
    }

    /**
     *
     * @return
     */
    public static DataFinderResultBuilder builder() {
        return new DataFinderResultBuilder<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFinderResult<?, ?> that = (DataFinderResult<?, ?>) o;
        return Double.compare(that.score, score) == 0 &&
                Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, resource);
    }

    @Override
    public String toString() {
        return "DataFinderResult{" +
                "score=" + score +
                ", resource=" + resource +
                '}';
    }

    /**
     *
     * @param <K>
     * @param <D>
     */
    public static final class DataFinderResultBuilder<K, D> {
        private double score;
        private DataResource<K, D> resource;

        private DataFinderResultBuilder() {
        }

        public DataFinderResultBuilder score(double score) {
            this.score = score;
            return this;
        }

        public DataFinderResultBuilder resource(DataResource<K, D> resource) {
            this.resource = resource;
            return this;
        }

        public DataFinderResult build() {
            return new DataFinderResult<>(score, resource);
        }
    }
}
