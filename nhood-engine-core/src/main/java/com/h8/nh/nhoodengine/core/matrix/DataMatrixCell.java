package com.h8.nh.nhoodengine.core.matrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class DataMatrixCell<R extends DataMatrixResource> {

    private final UUID uuid = UUID.randomUUID();

    private final double[] index;
    private final double[] dimensions;
    private final Set<DataMatrixCell<R>> children;
    private final Set<R> resources;

    public DataMatrixCell(final double[] index, final double[] dimensions) {
        if (index.length != dimensions.length) {
            throw new IllegalStateException("Index and dimensions arrays must have the same length");
        }
        this.index = Arrays.copyOf(index, index.length);
        this.dimensions = Arrays.copyOf(dimensions, dimensions.length);
        this.children = new HashSet<>();
        this.resources = new HashSet<>();
    }

    public void add(final R resource) {
        if (!this.hasKeyWithinRange(resource.getKey())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            findRelevantChild(resource).add(resource);
        } else {
            resources.add(resource);
        }
    }

    public Set<R> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    public Set<R> getResources(final R resource) {
        if (!this.hasKeyWithinRange(resource.getKey())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            return findRelevantChild(resource).getResources(resource);
        } else {
            return resources;
        }
    }

    private DataMatrixCell<R> findRelevantChild(final R resource) {
        return children.stream()
                .filter(c -> c.hasKeyWithinRange(resource.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no cell covering given key"));
    }

    public void split(final int factor) {
        if (hasChildren()) {
            throw new IllegalStateException("Cell has already been split");
        }

        int idx = 0;
        double width = 0.0;
        for (int i = 0; i < dimensions.length; i++) {
            if (dimensions[i] > width) {
                idx = i;
                width = dimensions[i];
            }
        }

        double size = dimensions[idx] / factor;
        for (int i = 0; i < factor; i++) {
            double[] index = Arrays.copyOf(this.index, this.index.length);
            index[idx] = this.index[idx] + i * size;
            double[] dimensions =  Arrays.copyOf(this.dimensions, this.dimensions.length);
            dimensions[idx] = size;
            DataMatrixCell<R> cell = new DataMatrixCell<>(index, dimensions);
            this.resources
                    .stream()
                    .filter(r -> cell.hasKeyWithinRange(r.getKey()))
                    .forEach(cell::add);
            this.children.add(cell);
        }

        this.resources.clear();
    }

    private boolean hasChildren() {
        return !children.isEmpty();
    }

    private boolean hasKeyWithinRange(final double[] key) {
        for (int i = 0; i < index.length; i++) {
            boolean isWithin = key[i] >= index[i] && key[i] < index[i] + dimensions[i];
            if (!isWithin) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataMatrixCell<?> that = (DataMatrixCell<?>) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "DataMatrixCell{" + uuid + '}';
    }
}
