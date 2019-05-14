package com.h8.nh.nhoodengine.core;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * This class is a wrapper of data resource returned by data finder.
 * Data is enriched with additional stats and metrics.
 *
 * @param <K> a type of data metadata key vector. Extends {@link DataResourceKey}.
 * @param <D> a generic type of data resource.
 */
public final class DataFinderResult<K extends DataResourceKey, D>
        implements Comparable<DataFinderResult<K, D>> {

    /**
     * An internal unique identifier;
     */
    private final UUID uuid;

    /**
     * A score which resource gained during the course of evaluation
     */
    private final BigDecimal score;

    /**
     * An actual data resource found
     */
    private final DataResource<K, D> resource;

    /**
     * A score which resource gained during the course of evaluation
     * @return actual score value
     */
    public BigDecimal getScore() {
        return score;
    }

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
    public DataFinderResult(final BigDecimal score, final DataResource<K, D> resource) {
        this.uuid = UUID.randomUUID();
        this.score = score;
        this.resource = resource;
    }

    /**
     * A static method exposing an auxiliary builder
     * @param keyClass key generic class
     * @param dataClass data generic class
     * @return An instance of a builder
     */
    public static <K extends DataResourceKey, D> DataFinderResultBuilder<K, D> builder(
            final Class<K> keyClass, final Class<D> dataClass) {
        return new DataFinderResultBuilder<>();
    }

    @Override
    public int compareTo(final DataFinderResult<K, D> o) {
        int result = score.compareTo(o.score);
        if (result != 0) {
            return result;
        }
        return uuid.compareTo(o.uuid);
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
        return Objects.equals(score, that.score)
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
     * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
     * @param <D> a generic type of data resource.
     */
    public static final class DataFinderResultBuilder<K extends DataResourceKey, D> {
        private BigDecimal score;
        private DataResource<K, D> resource;

        private DataFinderResultBuilder() {
        }

        /**
         * A score which resource gained during the course of evaluation
         * @param score score value
         * @return builder instance
         */
        public DataFinderResultBuilder<K, D> score(final BigDecimal score) {
            this.score = score;
            return this;
        }

        /**
         * An actual data resource found
         * @param resource resource value
         * @return builder instance
         */
        public DataFinderResultBuilder<K, D> resource(final DataResource<K, D> resource) {
            this.resource = resource;
            return this;
        }

        /**
         * Method will combine all gathered properties into DataFinderResult instance
         * @return a build instance of DataFinderResult
         */
        public DataFinderResult<K, D> build() {
            return new DataFinderResult<>(score, resource);
        }
    }
}
