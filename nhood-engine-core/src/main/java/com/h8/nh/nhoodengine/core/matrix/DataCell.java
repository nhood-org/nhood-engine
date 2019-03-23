package com.h8.nh.nhoodengine.core.matrix;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

final class DataCell<R> {

    private final Vector<Double> id;

    private final Set<R> resources;

    DataCell(final Vector<Double> id) {
        this.id = id;
        this.resources = new HashSet<>();
    }

    Vector<Double> getId() {
        return id;
    }

    Set<R> getResources() {
        return resources;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DataCell<?> dataCell = (DataCell<?>) o;
        return Objects.equals(id, dataCell.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DataCell{" + id + "}";
    }
}
