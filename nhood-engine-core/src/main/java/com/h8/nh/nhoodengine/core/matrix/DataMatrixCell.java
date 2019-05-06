package com.h8.nh.nhoodengine.core.matrix;

import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * @param <R>
 */
public final class DataMatrixCell<R extends DataMatrixResource> {

    private static final int DOUBLE_PRECISION = 1000;

    private final UUID uuid = UUID.randomUUID();

    private final double[] index;
    private final double[] dimensions;
    private final DoubleSummaryStatistics[] statistics;

    private final DataMatrixCell<R> parent;
    private final Set<DataMatrixCell<R>> children;
    private final Set<R> resources;

    private final DataMatrixCellConfiguration configuration;

    DataMatrixCell(
            final double[] index,
            final double[] dimensions,
            final DataMatrixCell<R> parent,
            final DataMatrixCellConfiguration configuration) {
        if (index.length != dimensions.length) {
            throw new IllegalStateException("Index and dimensions arrays must have the same length");
        }

        this.index = Arrays.copyOf(index, index.length);
        this.dimensions = Arrays.copyOf(dimensions, dimensions.length);

        this.statistics = new DoubleSummaryStatistics[index.length];
        Arrays.fill(this.statistics, new DoubleSummaryStatistics());

        this.parent = parent;
        this.children = new HashSet<>();
        this.resources = new HashSet<>();
        this.configuration = configuration;
    }

    public static <R extends DataMatrixResource> DataMatrixCell<R> root(
            final int size,
            final DataMatrixCellConfiguration configuration) {
        DataMatrixCell<R> cell = new DataMatrixCell<>(new double[size], new double[size], null, configuration);
        Arrays.fill(cell.index, -1 * configuration.getRootRange() / 2);
        Arrays.fill(cell.dimensions, configuration.getRootRange());
        return cell;
    }

    double[] getIndex() {
        return index;
    }

    double[] getDimensions() {
        return dimensions;
    }

    DataMatrixCell<R> getParent() {
        return parent;
    }

    boolean hasChildren() {
        return !children.isEmpty();
    }

    Set<DataMatrixCell<R>> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    boolean hasResources() {
        return !resources.isEmpty();
    }

    public Set<R> getResources() {
        return Collections.unmodifiableSet(resources);
    }

    boolean wrapsKey(final double[] key) {
        for (int i = 0; i < index.length; i++) {
            boolean isWithin = key[i] >= index[i] && key[i] < index[i] + dimensions[i];
            if (!isWithin) {
                return false;
            }
        }
        return true;
    }

    boolean wrapsKey(final double[] key, final double range) {
        for (int i = 0; i < index.length; i++) {
            boolean isWithin = key[i] - range >= index[i] && key[i] + range < index[i] + dimensions[i];
            if (!isWithin) {
                return false;
            }
        }
        return true;
    }

    double distanceFrom(final double[] key) {
        double sumOfSquares = 0.0;
        for (int i = 0; i < index.length; i++) {
            double d = Math.max(index[i] - key[i], key[i] - (index[i] + dimensions[i]));
            d = Math.max(0, d);
            sumOfSquares += d * d;
        }
        return Math.sqrt(sumOfSquares);
    }

    void add(final R resource) {
        if (!this.wrapsKey(resource.getKey())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            findRelevantChild(resource).add(resource);
        } else {
            resources.add(resource);
            updateStatistics(resource);
            split();
        }
    }

    private DataMatrixCell<R> findRelevantChild(final R resource) {
        return children.stream()
                .filter(c -> c.wrapsKey(resource.getKey()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no cell covering given key"));
    }

    private void updateStatistics(final R resource) {
        double[] key = resource.getKey();
        for (int i = 0; i < key.length; i++) {
            statistics[i].accept(key[i]);
        }
    }

    private void split() {
        if (hasChildren()) {
            throw new IllegalStateException("Cell has already been split");
        }

        if (this.resources.size() >= configuration.getCellSize()) {
            int idx = findWidestRangeDimension();
            split(idx);
        }
    }

    private void split(final int idx) {
        int iterations = this.configuration.getSplitIterations();

        Set<DataMatrixCell<R>> subCells = split(idx, iterations);
        this.children.addAll(subCells);

        this.resources
                .stream()
                .filter(r -> this.wrapsKey(r.getKey()))
                .forEach(this::add);
        this.resources.clear();
    }

    private Set<DataMatrixCell<R>> split(final int idx, final int iterations) {
        Set<DataMatrixCell<R>> subCells = new HashSet<>();
        subCells.add(this);

        int i = iterations;
        while (i > 0) {
            Set<DataMatrixCell<R>> created = new HashSet<>();
            for (DataMatrixCell<R> c : subCells) {
                double cutPoint = c.findCutPoint(idx);
                created.add(c.createLeftCell(idx, cutPoint, this));
                created.add(c.createRightCell(idx, cutPoint, this));
            }
            subCells = created;
            i--;
        }

        return subCells;
    }

    private DataMatrixCell<R> createLeftCell(
            final int idx,
            final double cutPoint,
            final DataMatrixCell<R> parent) {
        double[] leftIndex = Arrays.copyOf(this.index, this.index.length);
        leftIndex[idx] = this.index[idx];
        double[] leftDimensions = Arrays.copyOf(this.dimensions, this.dimensions.length);
        leftDimensions[idx] = cutPoint - this.index[idx];
        return new DataMatrixCell<>(leftIndex, leftDimensions, parent, this.configuration);
    }

    private DataMatrixCell<R> createRightCell(
            final int idx,
            final double cutPoint,
            final DataMatrixCell<R> parent) {
        double[] rightIndex = Arrays.copyOf(this.index, this.index.length);
        rightIndex[idx] = cutPoint;
        double[] rightDimensions = Arrays.copyOf(this.dimensions, this.dimensions.length);
        rightDimensions[idx] = this.index[idx] + this.dimensions[idx] - cutPoint;
        return new DataMatrixCell<>(rightIndex, rightDimensions, parent, this.configuration);
    }

    private int findWidestRangeDimension() {
        int idx = 0;
        double widestRange = 0.0;
        for (int i = 0; i < statistics.length; i++) {
            double range = statistics[i].getMax() - statistics[i].getMin();
            if (range > widestRange) {
                idx = i;
                widestRange = range;
            }
        }
        return idx;
    }

    private double findCutPoint(final int idx) {
        double cutPoint;
        if (statistics[idx].getCount() != 0) {
            cutPoint = statistics[idx].getAverage();
        } else {
            cutPoint = index[idx] + (dimensions[idx] / 2.0);
        }
        return round(cutPoint);
    }

    private double round(final double d) {
        double val = d;
        val = val * DOUBLE_PRECISION;
        val = (double) ((int) val);
        val = val / DOUBLE_PRECISION;
        return val;
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
