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
    private final DataMatrixCellConfiguration configuration;

    public DataMatrixCell(
            final int size,
            final DataMatrixCellConfiguration configuration) {
        this(new double[size], new double[size], configuration);
        Arrays.fill(this.index, Double.MIN_VALUE);
        Arrays.fill(this.dimensions, Double.MAX_VALUE);
    }

    DataMatrixCell(
            final double[] index,
            final double[] dimensions,
            final DataMatrixCellConfiguration configuration) {
        if (index.length != dimensions.length) {
            throw new IllegalStateException("Index and dimensions arrays must have the same length");
        }
        this.index = Arrays.copyOf(index, index.length);
        this.dimensions = Arrays.copyOf(dimensions, dimensions.length);
        this.configuration = configuration;
        this.children = new HashSet<>();
        this.resources = new HashSet<>();
    }

    double[] getIndex() {
        return index;
    }

    double[] getDimensions() {
        return dimensions;
    }

    Set<R> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    Set<DataMatrixCell<R>> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public DataMatrixCell<R> getClosestCell(final R resource) {
        if (!this.hasKeyWithinRange(resource.getKey())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            return findRelevantChild(resource).getClosestCell(resource);
        } else {
            return this;
        }
    }

    public void add(final R resource) {
        if (!this.hasKeyWithinRange(resource.getKey())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            findRelevantChild(resource).add(resource);
        } else {
            resources.add(resource);
            split();
        }
    }

    private DataMatrixCell<R> findRelevantChild(final R resource) {
        return children.stream()
                .filter(c -> c.hasKeyWithinRange(resource.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no cell covering given key"));
    }

    private void split() {
        if (hasChildren()) {
            throw new IllegalStateException("Cell has already been split");
        }

        if (this.resources.size() >= configuration.getCellSize()) {
            int idx = findWidestDimension();
            split(idx);
        }
    }

    private void split(final int idx) {
        int factor = configuration.getSplitFactor();
        double size = dimensions[idx] / factor;
        for (int i = 0; i < factor; i++) {
            double[] index = Arrays.copyOf(this.index, this.index.length);
            index[idx] = this.index[idx] + i * size;
            double[] dimensions =  Arrays.copyOf(this.dimensions, this.dimensions.length);
            dimensions[idx] = size;
            DataMatrixCell<R> cell = new DataMatrixCell<>(index, dimensions, this.configuration);
            this.resources
                    .stream()
                    .filter(r -> cell.hasKeyWithinRange(r.getKey()))
                    .forEach(cell::add);
            this.children.add(cell);
        }

        this.resources.clear();
    }

    private int findWidestDimension() {
        int idx = 0;
        double width = 0.0;
        for (int i = 0; i < dimensions.length; i++) {
            if (dimensions[i] > width) {
                idx = i;
                width = dimensions[i];
            }
        }
        return idx;
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
