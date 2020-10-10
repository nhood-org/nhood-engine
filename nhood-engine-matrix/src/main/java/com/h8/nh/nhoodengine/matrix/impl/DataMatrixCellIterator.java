package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

final class DataMatrixCellIterator<K extends DataResourceKey, D>
        implements DataMatrixResourceIterator<K, D> {

    private final BigDecimal[] entryPoint;
    private final DataMatrixCell<DataMatrixCellResource<K>> cell;
    private final Iterator<DataMatrixCell<DataMatrixCellResource<K>>> cellIterator;
    private final Map<UUID, DataResource<K, D>> data;

    private DataMatrixCellIterator<K, D> nestedCellIterator;

    private DataMatrixCell<DataMatrixCellResource<K>> current;
    private DataMatrixCell<DataMatrixCellResource<K>> next;

    private DataMatrixCellIterator(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<DataMatrixCellResource<K>> cell,
            final Map<UUID, DataResource<K, D>> data) {
        this.entryPoint = entryPoint;
        this.cell = cell;
        this.cellIterator = cell.getChildren()
                .stream()
                .sorted(Comparator.comparing(c -> c.distanceFrom(entryPoint)))
                .iterator();
        this.data = data;
        next = advance();
    }

    public static <K extends DataResourceKey, D> DataMatrixCellIterator<K, D> startWith(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<DataMatrixCellResource<K>> cell,
            final Map<UUID, DataResource<K, D>> data) {
        return new DataMatrixCellIterator<>(entryPoint, cell, data);
    }

    public Set<DataResource<K, D>> next() {
        current = nextCell();
        return current.getResources()
                .stream()
                .map(r -> data.get(r.getUuid()))
                .collect(Collectors.toSet());
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasNextWithinRange(final BigDecimal range) {
        return hasNext()
                && !currentWrapsAllPointsAroundTheEntryPointWithinRange(range)
                && (distanceFromEntryPointToNextIsWithinRange(range)
                || parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(range));
    }

    private DataMatrixCell<DataMatrixCellResource<K>> nextCell() {
        DataMatrixCell<DataMatrixCellResource<K>> result = next;
        do {
            next = advance();
        } while (next != null && !next.hasResources());
        return result;
    }

    private boolean currentWrapsAllPointsAroundTheEntryPointWithinRange(final BigDecimal range) {
        return current != null && current.wrapsKey(entryPoint, range);
    }

    private boolean distanceFromEntryPointToNextIsWithinRange(final BigDecimal range) {
        return next.distanceFrom(entryPoint).compareTo(range) <= 0;
    }

    private boolean parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(final BigDecimal range) {
        return next.getParent() != null && next.getParent().wrapsKey(entryPoint, range);
    }

    private DataMatrixCell<DataMatrixCellResource<K>> advance() {
        if (itIsInitialAdviseOfResourceCell()) {
            return cell;
        }
        if (!nestedIteratorHasNext() && nestedIteratorCanBeInitializer()) {
            nestedCellIterator = startWith(entryPoint, cellIterator.next(), data);
        }
        if (nestedIteratorHasNext()) {
            return nestedCellIterator.nextCell();
        }
        return null;
    }

    private boolean itIsInitialAdviseOfResourceCell() {
        return next == null && !cell.hasChildren();
    }

    private boolean nestedIteratorHasNext() {
        return nestedCellIterator != null && nestedCellIterator.hasNext();
    }

    private boolean nestedIteratorCanBeInitializer() {
        return cellIterator.hasNext();
    }
}
