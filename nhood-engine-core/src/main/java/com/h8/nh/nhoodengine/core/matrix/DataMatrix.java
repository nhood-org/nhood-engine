package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.DataFinderKeyMapper;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DataMatrix<K, D> implements DataMatrixRepository<K, D> {

    private static final int CELL_SIZE_LIMIT = 100 * 1000;

    private static final double MINIMAL_DELTA = 1.0E-07;

    private final Integer size;

    private final DataFinderKeyMapper<K> keyMapper;

    private final Vector<DataMatrixAxis> axes;

    private final Map<Vector<Double>, DataCell<DataResource<K, D>>> cells;

    private final Map<Vector<Double>, DataCellStatistics> cellsStatistics;

    public DataMatrix(final int size, final DataFinderKeyMapper<K> keyMapper) {
        this.size = size;
        this.keyMapper = keyMapper;

        this.axes = initializeAxes(size);

        Vector<Double> idx = initializeCellIndex(size);
        this.cells = new ConcurrentHashMap<>();
        this.cellsStatistics = new ConcurrentHashMap<>();
        this.cells.put(idx, new DataCell<>(idx));
        this.cellsStatistics.put(idx, new DataCellStatistics(idx));
    }

    private Vector<DataMatrixAxis> initializeAxes(final int size) {
        Vector<DataMatrixAxis> result = new Vector<>(size);
        for (int i = 0; i < size; i++) {
            result.add(new DataMatrixAxis(i));
        }
        return result;
    }

    private Vector<Double> initializeCellIndex(final int size) {
        Vector<Double> result = new Vector<>(size);
        for (int i = 0; i < size; i++) {
            result.add(i, Double.NEGATIVE_INFINITY);
        }
        return result;
    }

    @Override
    public int getMetadataSize() {
        return size;
    }

    // TODO!!!
    // synchronize
    @Override
    public void add(final DataResource<K, D> resource) {
        Vector<Double> metadata = getResourceKey(resource.getKey());

        Vector<Double> index = getCellIndex(metadata);
        if (!cellsStatistics.containsKey(index)) {
            cells.put(index, new DataCell<>(index));
        }

        DataCell<DataResource<K, D>> cell = cells.get(index);
        Set<DataResource<K, D>> resources = cell.getResources();
        resources.add(resource);

        if (!cellsStatistics.containsKey(index)) {
            cellsStatistics.put(index, new DataCellStatistics(index));
        }
        cellsStatistics.get(index).accept(metadata);

        if (resources.size() > CELL_SIZE_LIMIT) {
            split(cell);
        }
    }

    @Override
    public Set<DataResource<K, D>> findCell(
            final Vector<K> key) {
        Vector<Double> metadata = getResourceKey(key);
        Vector<Double> index = getCellIndex(metadata);
        return cells.get(index).getResources();
    }

    @Override
    public Set<DataResource<K, D>> findNeighbourCells(final Vector<K> key) {
        return null;
    }

    @Override
    public Set<DataResource<K, D>> findNeighbourCells(final Vector<K> key, final Double range) {
        Vector<Double> metadata = getResourceKey(key);
        Vector<Double> cellId = getCellIndex(metadata);

        Vector<Set<Double>> pointsPerIndex = findNeighbourPoints(metadata, range);
        Set<Vector<Double>> neighbours = combine(pointsPerIndex);

        return neighbours
                .stream()
                .map(this::getCellIndex)
                .filter(i -> !cellId.equals(i) && cells.containsKey(i))
                .map(cells::get)
                .flatMap(c -> c.getResources().stream())
                .collect(Collectors.toSet());
    }

    private Vector<Set<Double>> findNeighbourPoints(final Vector<Double> metadata, final Double range) {
        // TODO!!! refactor
        Vector<Set<Double>> pointsPerIndex = new Vector<>(metadata.size());
        for (int i = 0; i < metadata.size(); i++) {
            pointsPerIndex.add(i, new HashSet<>());
            Set<Double> points = pointsPerIndex.get(i);

            double point = metadata.get(i);
            points.add(point);

            DataMatrixAxisPoint axisPoint = axes.get(i).getCellAxisPoint(point);
            findPreviousNeighbourPoints(i, points, point, range, axisPoint);
            findNextNeighbourPoints(i, points, point, range, axisPoint);
        }
        return pointsPerIndex;
    }

    private void findPreviousNeighbourPoints(
            final int index,
            final Set<Double> points,
            final double point,
            final Double range,
            final DataMatrixAxisPoint axisPoint) {
        double lowerBound = axisPoint.getCellIndex();
        if (lowerBound == Double.NEGATIVE_INFINITY) {
            points.add(lowerBound);
            return;
        }
        if (point - lowerBound > range) {
            // TODO!!! refactor
            double previousPoint = lowerBound - MINIMAL_DELTA;
            if (axes.get(index).hasCellAxisPoint(previousPoint)) {
                points.add(previousPoint);
                DataMatrixAxisPoint previousAxisPoint = axes.get(index).getCellAxisPoint(previousPoint);
                findPreviousNeighbourPoints(index, points, point, range, previousAxisPoint);
            }
        }
    }

    private void findNextNeighbourPoints(
            final int index,
            final Set<Double> points,
            final double point,
            final Double range,
            final DataMatrixAxisPoint axisPoint) {
        double upperBound = axisPoint.getCellIndex() + axisPoint.getQuantumSize();
        if (upperBound - point <= range) {
            if (axes.get(index).hasCellAxisPoint(upperBound)) {
                points.add(upperBound);
                DataMatrixAxisPoint nextAxisPoint = axes.get(index).getCellAxisPoint(upperBound);
                findNextNeighbourPoints(index, points, point, range, nextAxisPoint);
            }
        }
    }

    private Set<Vector<Double>> combine(final Vector<Set<Double>> pointsPerIndex) {
        // TODO!!! refactor
        Set<Vector<Double>> neighbours = new HashSet<>();
        for (int i = 0; i < pointsPerIndex.size(); i++) {
            Set<Vector<Double>> previousLevel = neighbours;
            neighbours = new HashSet<>();
            Set<Double> points = pointsPerIndex.get(i);
            for (Double p : points) {
                if (i == 0) {
                    Vector<Double> m = new Vector<>(pointsPerIndex.size());
                    m.add(i, p);
                    neighbours.add(m);
                } else {
                    for (Vector<Double> v : previousLevel) {
                        Vector<Double> m = (Vector<Double>) v.clone();
                        m.add(i, p);
                        neighbours.add(m);
                    }
                }
            }
        }
        return neighbours;
    }

    private Vector<Double> getResourceKey(final Vector<K> metadata) {
        Vector<Double> result = new Vector<>(metadata.size());
        for (K m : metadata) {
            result.add(keyMapper.map(m));
        }
        return result;
    }

    private Vector<Double> getCellIndex(final Vector<Double> metadata) {
        Vector<Double> result = new Vector<>(metadata.size());
        for (int i = 0; i < metadata.size(); i++) {
            result.add(i, axes.get(i).getCellIndex(metadata.get(i)));
        }
        return result;
    }

    private void split(final DataCell<DataResource<K, D>> cell) {
        Vector<Double> cellId = cell.getId();
        if (!cellsStatistics.containsKey(cellId)) {
            throw new IllegalStateException(
                    "Matrix cell statistics are inconsistent");
        }

        // TODO!!! consider splitting by the widest side
        int index = cellsStatistics.get(cell.getId()).getWidestRangeIndex();
        axes.get(index).splitCell(cell.getId().get(index));

        if (!cells.containsKey(cellId)) {
            throw new IllegalStateException(
                    "Matrix cell statistics are inconsistent");
        }

        cells.put(cellId, new DataCell<>(cellId));
        cellsStatistics.put(cellId, new DataCellStatistics(cellId));
        for (DataResource<K, D> r : cell.getResources()) {
            add(r);
        }
    }
}
