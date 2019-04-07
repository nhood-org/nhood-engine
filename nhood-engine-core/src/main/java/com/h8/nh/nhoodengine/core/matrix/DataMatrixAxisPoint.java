package com.h8.nh.nhoodengine.core.matrix;

import java.util.Objects;

final class DataMatrixAxisPoint {

    private final Double cellIndex;

    private final Double quantumSize;

    DataMatrixAxisPoint(final Double cellIndex, final Double quantumSize) {
        this.cellIndex = cellIndex;
        this.quantumSize = quantumSize;
    }

    Double getCellIndex() {
        return cellIndex;
    }

    Double getQuantumSize() {
        return quantumSize;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataMatrixAxisPoint that = (DataMatrixAxisPoint) o;
        return Objects.equals(cellIndex, that.cellIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellIndex);
    }

    @Override
    public String toString() {
        return "DataMatrixAxisPoint{cellIndex=" + cellIndex + ", quantumSize=" + quantumSize + '}';
    }
}
