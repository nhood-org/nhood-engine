package com.h8.nh.nhoodengine.core.matrix;

/**
 * This is a configuration class for DataMatrixCell
 */
public final class DataMatrixCellConfiguration {

    /**
     * Default value of split iterations.
     * Used when value is not defined in the builder.
     */
    static final int DEFAULT_SPLIT_ITERATIONS = 2;

    /**
     * Default value of cell size.
     * Used when value is not defined in the builder.
     */
    static final int DEFAULT_MAX_CELL_SIZE = (int) Math.pow(10, 4);

    /**
     * Default value of root range.
     * Used when value is not defined in the builder.
     */
    static final double DEFAULT_ROOT_RANGE = Math.pow(10, 14);

    /**
     * Defines into how many times a single cell is split in half
     * example:
     *  for split iterations = 2 a single cell is split into 4 sub cells
     *  for split iterations = 4 a single cell is split into 8 sub cells
     */
    private int splitIterations;

    /**
     * Defines an inclusive limit of cell size. When exceeded a cell is split.
     */
    private int cellSize;

    /**
     * Defines an initial range of root cell.
     * Large values may impact metadata precision.
     */
    private double rootRange;

    private DataMatrixCellConfiguration(
            final int splitIterations,
            final int cellSize,
            final double rootRange) {
        this.splitIterations = splitIterations;
        this.cellSize = cellSize;
        this.rootRange = rootRange;
        validate();
    }

    /**
     * Defines into how many times a single cell is split in half
     * @return current split iterations value
     */
    public int getSplitIterations() {
        return splitIterations;
    }

    /**
     * Defines into how many times a single cell is split in half
     * @param splitIterations new split iterations value
     */
    public void setSplitIterations(final int splitIterations) {
        this.splitIterations = splitIterations;
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

    /**
     * Defines an initial range of root cell.
     * @return current root range value
     */
    public double getRootRange() {
        return rootRange;
    }

    /**
     * Defines an initial range of root cell.
     * @param rootRange new root range value
     */
    public void setRootRange(final double rootRange) {
        this.rootRange = rootRange;
        validate();
    }

    private void validate() {
        if (this.splitIterations <= 0) {
            throw new IllegalArgumentException("Split iterations must be greater than 0");
        }
        if (this.cellSize <= 1) {
            throw new IllegalArgumentException("Cell size must be greater than 1");
        }
        if (this.rootRange <= 0) {
            throw new IllegalArgumentException("Root range must be greater than 0");
        }
    }

    public static DataMatrixCellConfigurationBuilder builder() {
        return new DataMatrixCellConfigurationBuilder();
    }

    public static final class DataMatrixCellConfigurationBuilder {

        private int splitIterations;
        private int cellSize;
        private double rootRange;

        private DataMatrixCellConfigurationBuilder() {
            splitIterations = DEFAULT_SPLIT_ITERATIONS;
            cellSize = DEFAULT_MAX_CELL_SIZE;
            rootRange = DEFAULT_ROOT_RANGE;
        }

        public DataMatrixCellConfigurationBuilder splitIterations(final int splitIterations) {
            this.splitIterations = splitIterations;
            return this;
        }

        public DataMatrixCellConfigurationBuilder cellSize(final int cellSize) {
            this.cellSize = cellSize;
            return this;
        }

        public DataMatrixCellConfigurationBuilder rootRange(final double rootRange) {
            this.rootRange = rootRange;
            return this;
        }

        public DataMatrixCellConfiguration build() {
            return new DataMatrixCellConfiguration(splitIterations, cellSize, rootRange);
        }
    }
}
