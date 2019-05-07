package com.h8.nh.nhoodengine.core.matrix;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Iterator;

public final class DataMatrixCellIterator<R extends DataMatrixResource> {

    private final BigDecimal[] entryPoint;
    private final DataMatrixCell<R> cell;
    private final Iterator<DataMatrixCell<R>> cellIterator;

    private DataMatrixCellIterator<R> nestedCellIterator;
    private DataMatrixCell<R> next;

    private DataMatrixCellIterator(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<R> cell) {
        this.entryPoint = entryPoint;
        this.cell = cell;
        this.cellIterator = cell.getChildren()
                .stream()
                .sorted(Comparator.comparing(c -> c.distanceFrom(entryPoint)))
                .iterator();
        next = advance();
    }

    public static <R extends DataMatrixResource> DataMatrixCellIterator<R> startWith(
            final BigDecimal[] entryPoint,
            final DataMatrixCell<R> cell) {
        return new DataMatrixCellIterator<>(entryPoint, cell);
    }

    public DataMatrixCell<R> next() {
        DataMatrixCell<R> result = next;
        do {
            next = advance();
        } while (next != null && !next.hasResources());
        return result;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasNextWithinRange(final BigDecimal range) {
        return hasNext()
                && (distanceFromEntryPointToNextIsWithinRange(range)
                        || !parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(range));
    }

    private boolean distanceFromEntryPointToNextIsWithinRange(final BigDecimal range) {
        return next.distanceFrom(entryPoint).compareTo(range) <= 0;
    }

    private boolean parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(final BigDecimal range) {
        return next.getParent() != null && next.getParent().wrapsKey(entryPoint, range);
    }

    private DataMatrixCell<R> advance() {
        if (itIsInitialAdviseOfResourceCell()) {
            return cell;
        }
        if (!nestedIteratorHasNext() && nestedIteratorCanBeInitializer()) {
            nestedCellIterator = startWith(entryPoint, cellIterator.next());
        }
        if (nestedIteratorHasNext()) {
            return nestedCellIterator.next();
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
