package com.h8.nh.nhoodengine.core.matrix;

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

}
