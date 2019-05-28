package com.h8.nh.nhoodengine.matrix.impl.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import com.h8.nh.nhoodengine.matrix.DataMatrixResourceIterator;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;

public final class DataMatrixCellIterator<K extends DataResourceKey, D>
        implements DataMatrixResourceIterator<K, D> {

    private final BigDecimal[] entryPoint;
    private final DataMatrixCell<DataResource<K, D>> cell;
    private final Iterator<DataMatrixCell<DataResource<K, D>>> cellIterator;

    private DataMatrixCellIterator<K, D> nestedCellIterator;
    private DataMatrixCell<DataResource<K, D>> next;

    private DataMatrixCellIterator(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<DataResource<K, D>> cell) {
        this.entryPoint = entryPoint;
        this.cell = cell;
        this.cellIterator = cell.getChildren()
                .stream()
                .sorted(Comparator.comparing(c -> c.distanceFrom(entryPoint)))
                .iterator();
        next = advance();
    }

    public static <K extends DataResourceKey, D> DataMatrixCellIterator<K, D> startWith(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<DataResource<K, D>> cell) {
        return new DataMatrixCellIterator<>(entryPoint, cell);
    }

    public Set<DataResource<K, D>> next() {
        return nextCell().getResources();
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasNextWithinRange(final BigDecimal range) {
        return hasNext()
                && (distanceFromEntryPointToNextIsWithinRange(range)
                || !parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(range));
    }

    private DataMatrixCell<DataResource<K, D>> nextCell() {
        DataMatrixCell<DataResource<K, D>> result = next;
        do {
            next = advance();
        } while (next != null && !next.hasResources());
        return result;
    }

    private boolean distanceFromEntryPointToNextIsWithinRange(final BigDecimal range) {
        return next.distanceFrom(entryPoint).compareTo(range) <= 0;
    }

    private boolean parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(final BigDecimal range) {
        return next.getParent() != null && next.getParent().wrapsKey(entryPoint, range);
    }

    private DataMatrixCell<DataResource<K, D>> advance() {
        if (itIsInitialAdviseOfResourceCell()) {
            return cell;
        }
        if (!nestedIteratorHasNext() && nestedIteratorCanBeInitializer()) {
            nestedCellIterator = startWith(entryPoint, cellIterator.next());
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
