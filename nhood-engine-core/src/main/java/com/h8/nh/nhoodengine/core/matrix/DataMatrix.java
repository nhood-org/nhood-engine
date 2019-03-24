package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.DataFinderKeyMapper;
import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;

import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

final class DataMatrix<K, D> implements DataMatrixRepository<K, D> {

    private static final int CELL_SIZE_LIMIT = 1000;

    private final Integer size;

    private final DataFinderKeyMapper<K> keyMapper;

    private final Vector<DataMatrixAxis> axes;

    private final Map<Vector<Double>, DataCell<DataResource<K, D>>> cells;

    private final Map<Vector<Double>, DataCellStatistics> cellsStatistics;

    DataMatrix(final int size, final DataFinderKeyMapper<K> keyMapper) {
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

    @Override
    public void add(final DataResource<K, D> resource) {
        Vector<Double> index = getCellIndex(resource.getKey());
        DataCell<DataResource<K, D>> cell = cells.get(index);

        Set<DataResource<K, D>> resources = cell.getResources();
        resources.add(resource);

        if (resources.size() > CELL_SIZE_LIMIT) {
            split(cell);
        }
    }

    @Override
    public Set<DataResource<K, D>> findCell(
            final Vector<K> metadata) {
        Vector<Double> index = getCellIndex(metadata);
        return cells.get(index).getResources();
    }

    @Override
    public Set<DataResource<K, D>> findNeighbourCells(
            final Vector<K> metadata, final Double range) {
        Vector<Double> index = getCellIndex(metadata);
        return cells.get(index).getResources();
        // TODO!!!
    }

    private Vector<Double> getCellIndex(final Vector<K> metadata) {
        Vector<Double> result = new Vector<>(metadata.size());
        for (int i = 0; i < metadata.size(); i++) {
            double k = keyMapper.map(metadata.get(i));
            result.add(i, axes.get(i).getCellIndex(k));
        }
        return result;
    }

    private void split(final DataCell<DataResource<K, D>> cell) {
        Vector<Double> cellId = cell.getId();
        if (!cellsStatistics.containsKey(cellId)) {
            throw new IllegalStateException(
                    "Matrix cell statistics are inconsistent");
        }

        int index = cellsStatistics.get(cell.getId()).getLowestStandardDeviationIndex();
        axes.get(index).splitCell(cell.getId().get(index));
        // TODO!!!
    }

}
