package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_ROUNDING_MODE;
import static com.h8.nh.nhoodengine.core.DataResourceKey.UNIFIED_BIG_DECIMAL_SCALE;

final class DataMatrixCell<R extends DataResource<?, ?>> {

    private final UUID uuid = UUID.randomUUID();
    private final DataMatrixCellConfiguration configuration;

    private final BigDecimal[] index;
    private final BigDecimal[] closure;
    private final DoubleSummaryStatistics[] statistics;

    private final DataMatrixCell<R> parent;
    private final Set<DataMatrixCell<R>> children;

    private final HashSet<R> resources;

    DataMatrixCell(
            final BigDecimal[] index,
            final BigDecimal[] closure,
            final DataMatrixCell<R> parent,
            final DataMatrixCellConfiguration configuration) {
        if (index.length != closure.length) {
            throw new IllegalStateException("Index and closure arrays must have the same length");
        }

        this.index = Arrays.copyOf(index, index.length);
        this.closure = Arrays.copyOf(closure, closure.length);

        this.statistics = new DoubleSummaryStatistics[index.length];
        for (int i = 0; i < this.statistics.length; i++) {
            this.statistics[i] = new DoubleSummaryStatistics();
        }

        this.parent = parent;
        this.children = new HashSet<>();
        this.resources = new HashSet<>();
        this.configuration = configuration;

    }

    BigDecimal[] getIndex() {
        return index;
    }

    BigDecimal[] getClosure() {
        return closure;
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

    @SuppressWarnings("unchecked")
    Set<R> getResources() {
        synchronized (this) {
            return (Set<R>) resources.clone();
        }
    }

    boolean wrapsKey(final BigDecimal[] key) {
        for (int i = 0; i < index.length; i++) {
            boolean isWithin =
                    key[i].compareTo(index[i]) >= 0
                            && key[i].compareTo(closure[i]) < 0;
            if (!isWithin) {
                return false;
            }
        }
        return true;
    }

    boolean wrapsKey(final BigDecimal[] key, final BigDecimal range) {
        for (int i = 0; i < index.length; i++) {
            boolean isWithin =
                    key[i].subtract(range).compareTo(index[i]) >= 0
                            && key[i].add(range).compareTo(closure[i]) < 0;
            if (!isWithin) {
                return false;
            }
        }
        return true;
    }

    BigDecimal distanceFrom(final BigDecimal[] key) {
        BigDecimal sumOfSquares = BigDecimal.ZERO;
        for (int i = 0; i < index.length; i++) {
            BigDecimal d = BigDecimal.ZERO;
            d = d.max(index[i].subtract(key[i]));
            d = d.max(key[i].subtract(closure[i]));
            sumOfSquares = sumOfSquares.add(d.pow(2));
        }
        return BigDecimal.valueOf(Math.sqrt(sumOfSquares.doubleValue()))
                .setScale(UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);
    }

    public void add(final R resource) {
        if (!this.wrapsKey(resource.getKey().unified())) {
            throw new IllegalStateException("Cell does not cover given key");
        }
        if (this.hasChildren()) {
            findRelevantChild(resource).add(resource);
            return;
        }
        synchronized (this) {
            if (this.hasChildren()) {
                findRelevantChild(resource).add(resource);
            } else {
                resources.add(resource);
                updateStatistics(resource);
                split();
            }
        }
    }

    private DataMatrixCell<R> findRelevantChild(final R resource) {
        return children.stream()
                .filter(c -> c.wrapsKey(resource.getKey().unified()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("There is no cell covering given key"));
    }

    private void updateStatistics(final R resource) {
        BigDecimal[] key = resource.getKey().unified();
        for (int i = 0; i < key.length; i++) {
            statistics[i].accept(key[i].doubleValue());
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
                .filter(r -> this.wrapsKey(r.getKey().unified()))
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
                BigDecimal splitPoint = c.computeSplitPoint(idx);
                created.add(c.createLeftCell(idx, splitPoint, this));
                created.add(c.createRightCell(idx, splitPoint, this));
            }
            subCells = created;
            i--;
        }

        return subCells;
    }

    private DataMatrixCell<R> createLeftCell(
            final int idx,
            final BigDecimal splitPoint,
            final DataMatrixCell<R> parent) {
        BigDecimal[] leftIndex = Arrays.copyOf(this.index, this.index.length);
        BigDecimal[] leftDimensions = Arrays.copyOf(this.closure, this.closure.length);
        leftDimensions[idx] = splitPoint;
        return new DataMatrixCell<>(leftIndex, leftDimensions, parent, this.configuration);
    }

    private DataMatrixCell<R> createRightCell(
            final int idx,
            final BigDecimal splitPoint,
            final DataMatrixCell<R> parent) {
        BigDecimal[] rightIndex = Arrays.copyOf(this.index, this.index.length);
        rightIndex[idx] = splitPoint;
        BigDecimal[] rightDimensions = Arrays.copyOf(this.closure, this.closure.length);
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

    private BigDecimal computeSplitPoint(final int idx) {
        if (statistics[idx].getCount() != 0) {
            return BigDecimal.valueOf(statistics[idx].getAverage());
        } else {
            return index[idx].add(closure[idx])
                    .divide(BigDecimal.valueOf(2), UNIFIED_BIG_DECIMAL_SCALE, UNIFIED_BIG_DECIMAL_ROUNDING_MODE);
        }
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
