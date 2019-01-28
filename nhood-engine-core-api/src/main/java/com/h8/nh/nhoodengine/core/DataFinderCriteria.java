package com.h8.nh.nhoodengine.core;

import java.util.Objects;
import java.util.Vector;

public class DataFinderCriteria<K> {

    private final Vector<K> metadata;

    private final int limit;

    public DataFinderCriteria(Vector<K> metadata, int limit) {
        this.metadata = metadata;
        this.limit = limit;
    }

    public Vector<K> getMetadata() {
        return metadata;
    }

    public int getLimit() {
        return limit;
    }

    public static DataFinderCriteriaBuilder builder() {
        return new DataFinderCriteriaBuilder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFinderCriteria<?> that = (DataFinderCriteria<?>) o;
        return limit == that.limit &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, limit);
    }

    @Override
    public String toString() {
        return "DataFinderCriteria{" +
                "metadata=" + metadata +
                ", limit=" + limit +
                '}';
    }

    public static final class DataFinderCriteriaBuilder<K> {
        private Vector<K> metadata;
        private int limit;

        private DataFinderCriteriaBuilder() {
        }

        public DataFinderCriteriaBuilder metadata(Vector<K> metadata) {
            this.metadata = metadata;
            return this;
        }

        public DataFinderCriteriaBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public DataFinderCriteria build() {
            return new DataFinderCriteria<>(metadata, limit);
        }
    }
}
