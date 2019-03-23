package com.h8.nh.nhoodengine.core.matrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Double axisRangeMin = 0.0;

    private Double axisRangeMax = 0.0;

    private Double quantumSize = DEFAULT_QUANTUM_SIZE;

    DataMatrixAxis(final Integer index) {
        this.index = index;

        DataMatrixAxisPoint p = new DataMatrixAxisPoint(Double.NEGATIVE_INFINITY, quantumSize);
        this.cellIndices = new HashMap<>();
        this.cellIndices.put(0.0, p);
    }

    Integer getIndex() {
        return index;
    }

    List<Double> splitCell(final Double coordinate) {
        List<Double> result = new ArrayList<>();

        double quantizedCoordinate = coordinate - (coordinate % quantumSize);

        if (cellIndices.containsKey(quantizedCoordinate)) {

            double size = cellIndices.get(quantizedCoordinate).getQuantumSize() / SPLIT_FACTOR;
            for (int i = 0; i < SPLIT_FACTOR; i++) {

                double cellIndex = quantizedCoordinate + (i * size);
                DataMatrixAxisPoint p = new DataMatrixAxisPoint(cellIndex, size);
                cellIndices.put(cellIndex, p);

                result.add(cellIndex);
            }

            return result;

        } else if (quantizedCoordinate < axisRangeMin) {

            double lowerBoundCoordinate = quantizedCoordinate - EXPANSION_FACTOR * quantumSize;
            for (double i = lowerBoundCoordinate; i < quantizedCoordinate; i = i + quantumSize) {

                double index = cellIndices.get(axisRangeMin).getCellIndex();
                DataMatrixAxisPoint p = new DataMatrixAxisPoint(index, quantumSize);
                cellIndices.put(i, p);

                result.add(i);
            }

            double upperBoundCoordinate = axisRangeMin;
            for (double i = quantizedCoordinate; i <= upperBoundCoordinate; i = i + quantumSize) {

                DataMatrixAxisPoint p = new DataMatrixAxisPoint(quantizedCoordinate, quantumSize);
                cellIndices.put(i, p);

                result.add(i);
            }

            axisRangeMin = lowerBoundCoordinate;

            return result;

        } else if (quantizedCoordinate > axisRangeMax) {

            double lowerBoundCoordinate = axisRangeMax;
            for (double i = lowerBoundCoordinate; i < quantizedCoordinate; i = i + quantumSize) {

                double index = cellIndices.get(axisRangeMax).getCellIndex();
                DataMatrixAxisPoint p = new DataMatrixAxisPoint(index, quantumSize);
                cellIndices.put(i, p);

                result.add(i);
            }

            double upperBoundCoordinate = quantizedCoordinate + EXPANSION_FACTOR * quantumSize;
            for (double i = quantizedCoordinate; i <= upperBoundCoordinate; i = i + quantumSize) {

                DataMatrixAxisPoint p = new DataMatrixAxisPoint(quantizedCoordinate, quantumSize);
                cellIndices.put(i, p);

                result.add(i);
            }

            axisRangeMax = upperBoundCoordinate;

            return result;

        }
        throw new IllegalStateException(
                "Axis within its lower and upper bounds is inconsistent");
    }

    Double getCellIndex(final Double coordinate) {
        double quantizedCoordinate = coordinate - (coordinate % quantumSize);
        if (cellIndices.containsKey(quantizedCoordinate)) {
            return getCellIndex(coordinate, quantumSize);
        } else if (quantizedCoordinate < axisRangeMin) {
            return getCellIndex(axisRangeMin, quantumSize);
        } else if (quantizedCoordinate > axisRangeMax) {
            return cellIndices.get(axisRangeMax).getCellIndex();
        }
        throw new IllegalStateException(
                "Axis within its lower and upper bounds is inconsistent");
    }

    private Double getCellIndex(final Double coordinate, final Double quantumSize) {
        double quantizedCoordinate = coordinate - (coordinate % quantumSize);
        if (!cellIndices.containsKey(quantizedCoordinate)) {
            throw new IllegalStateException(
                    "Axis within its lower and upper bounds is inconsistent");
        }

        DataMatrixAxisPoint p = cellIndices.get(quantizedCoordinate);
        if (quantumSize.equals(p.getQuantumSize())) {
            return p.getCellIndex();
        }

        return getCellIndex(coordinate, quantumSize / SPLIT_FACTOR);
    }

}
