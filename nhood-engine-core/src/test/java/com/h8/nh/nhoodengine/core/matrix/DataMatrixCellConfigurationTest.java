package com.h8.nh.nhoodengine.core.matrix;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixCellConfigurationTest {

    @Test
    void shouldBuildNewConfigurationWithDefaultValues() {
        // when
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .build();

        // then
        assertThat(configuration.getCellSize())
                .isEqualTo(DataMatrixCellConfiguration.DEFAULT_MAX_CELL_SIZE);
        assertThat(configuration.getSplitFactor())
                .isEqualTo(DataMatrixCellConfiguration.DEFAULT_SPLIT_FACTOR);
    }

    @Test
    void shouldBuildNewConfigurationWithGivenValues() {
        // given
        int cellSize = 2;
        int splitFactor = 2;

        // when
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitFactor(splitFactor)
                .build();

        // then
        assertThat(configuration.getCellSize())
                .isEqualTo(cellSize);
        assertThat(configuration.getSplitFactor())
                .isEqualTo(splitFactor);
    }

    @Test
    void shouldValidateIllegalSplitFactorWhenConstructed() {
        // given
        int cellSize = 2;
        int splitFactor = 1;

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitFactor(splitFactor)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Split factor must be greater that 1")
                .hasNoCause();
    }

    @Test
    void shouldValidateIllegalSplitFactorWhenModified() {
        // given
        int cellSize = 2;
        int splitFactor = 2;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitFactor(splitFactor)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setSplitFactor(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Split factor must be greater that 1")
                .hasNoCause();
    }

    @Test
    void shouldValidateIllegalCellSizeWhenConstructed() {
        // given
        int cellSize = 1;
        int splitFactor = 2;

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitFactor(splitFactor)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cell size must be greater that 1")
                .hasNoCause();

    }

    @Test
    void shouldValidateIllegalCellSizeWhenModified() {
        // given
        int cellSize = 2;
        int splitFactor = 2;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitFactor(splitFactor)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setCellSize(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cell size must be greater that 1")
                .hasNoCause();
    }
}
