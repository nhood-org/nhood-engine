package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a simple data resource that is stored within a matrix data cell.
 *
 * DataMatrixCellResource consists of two elements:
 * - uuid identifier
 * - a vector key that represents metadata of data resource
 *
 * @param <K> a generic type of data metadata key vector. Extends {@link DataResourceKey}.
 */
public final class DataMatrixCellResource<K extends DataResourceKey>
        implements Comparable<DataMatrixCellResource<K>> {

    /**
     * A unique identifier
     */
    private final UUID uuid;

    /**
     * Data metadata key vector
     */
    private final K key;

    private DataMatrixCellResource(final UUID uuid, final K key) {
        this.uuid = uuid;
        this.key = key;
    }

    /**
     * Default constructor that maps {@link DataResource} into cell resource.
     * @param resource data resource
     */
    public static <K extends DataResourceKey> DataMatrixCellResource<K> form(DataResource<K, ?> resource) {
        return new DataMatrixCellResource<>(
                resource.getUuid(),
                resource.getKey()
        );
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

    @Override
    public int compareTo(final DataMatrixCellResource<K> o) {
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
        DataMatrixCellResource<?> that = (DataMatrixCellResource<?>) o;
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
                + '}';
    }
}
