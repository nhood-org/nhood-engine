package com.h8.nh.nhoodengine.core.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1. quantized indexation of cells
 * <p>
 * cells:   |    |  | | |
 * indices: |0   |4 |6|7|
 * <p>
 * 2. axis adaptive quantization
 * 3. axis adaptive range expansion
 */
final class DataMatrixAxis {

    private static final Integer SPLIT_FACTOR = 2;
    private static final Integer EXPANSION_FACTOR = 2;
    private static final Double DEFAULT_QUANTUM_SIZE = 1000.0 * SPLIT_FACTOR;

    private final Integer index;

    private final Map<Double, DataMatrixAxisPoint> cellIndices;

    private Double rangeMin = 0.0;

    private Double rangeMax = 0.0;

    private Double quantumSize = DEFAULT_QUANTUM_SIZE;

    DataMatrixAxis(final Integer index) {
        this.index = index;

        DataMatrixAxisPoint p = new DataMatrixAxisPoint(Double.NEGATIVE_INFINITY, quantumSize);
        this.cellIndices = new ConcurrentHashMap<>();
        this.cellIndices.put(0.0, p);
    }

    Integer getIndex() {
        return index;
    }

    List<DataMatrixAxisPoint> splitCell(final double quantizedCoordinate) {
        List<DataMatrixAxisPoint> result = new ArrayList<>();
        if (cellIndices.containsKey(quantizedCoordinate)) {
            return splitCell(result, quantizedCoordinate);
        } else if (quantizedCoordinate < rangeMin) {
            return expandNegativeSide(result);
        } else if (quantizedCoordinate > rangeMax) {
            return expandPositiveSide(result);
        }
        throw new IllegalStateException(
                "Axis within its lower and upper bounds is inconsistent");
    }

    Double getCellIndex(final double coordinate) {
        double quantizedCoordinate = quantize(coordinate, quantumSize);
        if (cellIndices.containsKey(quantizedCoordinate)) {
            return getCellIndex(coordinate, quantumSize);
        } else if (quantizedCoordinate < rangeMin) {
            return getCellIndex(rangeMin, quantumSize);
        } else if (quantizedCoordinate > rangeMax) {
            return cellIndices.get(rangeMax).getCellIndex();
        }
        throw new IllegalStateException(
                "Axis within its lower and upper bounds is inconsistent");
    }

    // TODO!!!
    // synchronize
    private List<DataMatrixAxisPoint> splitCell(
            final List<DataMatrixAxisPoint> result,
            final double quantizedCoordinate) {
        double size = cellIndices.get(quantizedCoordinate).getQuantumSize() / SPLIT_FACTOR;
        for (int i = 0; i < SPLIT_FACTOR; i++) {
            double cellIndex = quantizedCoordinate + (i * size);
            DataMatrixAxisPoint p = new DataMatrixAxisPoint(cellIndex, size);
            cellIndices.put(cellIndex, p);
            result.add(p);
        }
        return result;
    }

    // TODO!!!
    // synchronize
    private List<DataMatrixAxisPoint> expandNegativeSide(
            final List<DataMatrixAxisPoint> result) {
        double lowerBoundCoordinate = rangeMin - EXPANSION_FACTOR * quantumSize;
        fillRangeWithIndex(result, lowerBoundCoordinate, rangeMin);
        rangeMin = lowerBoundCoordinate;
        return result;
    }

    // TODO!!!
    // synchronize
    private List<DataMatrixAxisPoint> expandPositiveSide(
            final List<DataMatrixAxisPoint> result) {
        double upperBoundCoordinate = rangeMax + EXPANSION_FACTOR * quantumSize;
        fillRangeWithIndex(result, rangeMax, upperBoundCoordinate);
        rangeMax = upperBoundCoordinate;
        return result;
    }

    private void fillRangeWithIndex(
            final List<DataMatrixAxisPoint> result,
            final double inclusiveLoweBoundCoordinate,
            final double exclusiveUpperBoundCoordinate) {
        for (double i = inclusiveLoweBoundCoordinate; i <= exclusiveUpperBoundCoordinate; i = i + quantumSize) {
            DataMatrixAxisPoint p = new DataMatrixAxisPoint(i, quantumSize);
            cellIndices.put(i, p);
            result.add(p);
        }
    }

    private Double getCellIndex(final double coordinate, final double quantumSize) {
        double quantizedCoordinate = quantize(coordinate, quantumSize);
        if (!cellIndices.containsKey(quantizedCoordinate)) {
            throw new IllegalStateException(
                    "Axis within its lower and upper bounds is inconsistent");
        }

        DataMatrixAxisPoint p = cellIndices.get(quantizedCoordinate);
        if (Double.compare(quantumSize, p.getQuantumSize()) == 0) {
            return p.getCellIndex();
        }

        return getCellIndex(coordinate, quantumSize / SPLIT_FACTOR);
    }

    private Double quantize(final double coordinate, final double quantumSize) {
        if (coordinate == Double.NEGATIVE_INFINITY) {
            return coordinate;
        }
        if (coordinate == Double.POSITIVE_INFINITY) {
            return coordinate;
        }
        if (coordinate < 0) {
            return coordinate - (coordinate % quantumSize) - quantumSize;
        }
        return coordinate - (coordinate % quantumSize);
    }

}
