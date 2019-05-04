package com.h8.nh.nhoodengine.core.matrix;

/**
 * This is a configuration class for DataMatrixCell
 */
public final class DataMatrixCellConfiguration {

    /**
     * Default value of split factor.
     * Used when value is not defined in the builder.
     */
    static final int DEFAULT_SPLIT_FACTOR = 10;

    /**
     * Default value of cell size.
     * Used when value is not defined in the builder.
     */
    static final int DEFAULT_MAX_CELL_SIZE = 10 * 1000;

    /**
     * Defines into how many cells a single cell is split
     */
    private int splitFactor;

    /**
     * Defines an inclusive limit of cell size. When exceeded a cell is split.
     */
    private int cellSize;

    private DataMatrixCellConfiguration(final int splitFactor, final int cellSize) {
        this.splitFactor = splitFactor;
        this.cellSize = cellSize;
        validate();
    }

    /**
     * Defines into how many cells a single cell is split
     * @return current split factor value
     */
    public int getSplitFactor() {
        return splitFactor;
    }

    /**
     * Defines into how many cells a single cell is split
     * @param splitFactor new split factor value
     */
    public void setSplitFactor(final int splitFactor) {
        this.splitFactor = splitFactor;
        validate();
    }

    /**
     * Defines an inclusive limit of cell size. When exceeded a cell is split.
     * @return current cell size
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Defines an inclusive limit of cell size. When exceeded a cell is split.
     * @param cellSize new cell size value
     */
    public void setCellSize(final int cellSize) {
        this.cellSize = cellSize;
        validate();
    }

    private void validate() {
        if (this.splitFactor <= 1) {
            throw new IllegalArgumentException("Split factor must be greater that 1");
        }
        if (this.cellSize <= 1) {
            throw new IllegalArgumentException("Cell size must be greater that 1");
        }
    }

    public static DataMatrixCellConfigurationBuilder builder() {
        return new DataMatrixCellConfigurationBuilder();
    }

    public static final class DataMatrixCellConfigurationBuilder {

        private int splitFactor;
        private int cellSize;

        private DataMatrixCellConfigurationBuilder() {
            splitFactor = DEFAULT_SPLIT_FACTOR;
            cellSize = DEFAULT_MAX_CELL_SIZE;
        }

        public DataMatrixCellConfigurationBuilder splitFactor(final int splitFactor) {
            this.splitFactor = splitFactor;
            return this;
        }

        public DataMatrixCellConfigurationBuilder cellSize(final int cellSize) {
            this.cellSize = cellSize;
            return this;
        }

        public DataMatrixCellConfiguration build() {
            return new DataMatrixCellConfiguration(splitFactor, cellSize);
        }
    }
}
