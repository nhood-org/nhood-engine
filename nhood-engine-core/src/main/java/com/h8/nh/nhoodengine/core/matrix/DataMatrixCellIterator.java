package com.h8.nh.nhoodengine.core.matrix;

import java.util.Comparator;
import java.util.Iterator;

public final class DataMatrixCellIterator<R extends DataMatrixResource> {

    private final double[] entryPoint;
    private final DataMatrixCell<R> cell;
    private final Iterator<DataMatrixCell<R>> cellIterator;

    private DataMatrixCellIterator<R> nestedCellIterator;
    private DataMatrixCell<R> next;

    private DataMatrixCellIterator(
            final double[] entryPoint,
            final DataMatrixCell<R> cell) {
        this.entryPoint = entryPoint;
        this.cell = cell;
        this.cellIterator = cell.getChildren()
                .stream()
                .sorted(Comparator.comparingDouble(c -> c.distanceFrom(this.entryPoint)))
                .iterator();
        next = advance();
    }

    public <R extends DataMatrixResource> DataMatrixCellIterator<R> startWith(
            final double[] entryPoint,
            final DataMatrixCell<R> cell) {
        return new DataMatrixCellIterator<>(entryPoint, cell);
    }

    public DataMatrixCell<R> next() {
        DataMatrixCell<R> result = next;
        next = advance();
        return result;
    }

    public boolean hasNext() {
        return next != null;
    }

    public boolean hasNextWithinRange(final double range) {
        return hasNext()
                && (distanceFromEntryPointToNextIsWithinRange(range)
                        || !parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(range));
    }

    private boolean distanceFromEntryPointToNextIsWithinRange(final double range) {
        return next.distanceFrom(entryPoint) <= range;
    }

    private boolean parentOfNextWrapsAllPointsAroundTheEntryPointWithinRange(final double range) {
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
