package com.h8.nh.nhoodengine.matrix.impl;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

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
        assertThat(configuration.getSplitIterations())
                .isEqualTo(DataMatrixCellConfiguration.DEFAULT_SPLIT_ITERATIONS);
        assertThat(configuration.getRootRange())
                .isEqualTo(DataMatrixCellConfiguration.DEFAULT_ROOT_RANGE);
    }

    @Test
    void shouldBuildNewConfigurationWithGivenValues() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        BigDecimal rootRange = BigDecimal.valueOf(1000);

        // when
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .rootRange(rootRange)
                .build();

        // then
        assertThat(configuration.getCellSize())
                .isEqualTo(cellSize);
        assertThat(configuration.getSplitIterations())
                .isEqualTo(splitIterations);
        assertThat(configuration.getRootRange())
                .isEqualTo(rootRange);
    }

    @Test
    void shouldValidateIllegalSplitIterationsWhenConstructed() {
        // given
        int cellSize = 2;
        int splitIterations = 0;

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Split iterations must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateIllegalSplitIterationsWhenModified() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setSplitIterations(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Split iterations must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateIllegalCellSizeWhenConstructed() {
        // given
        int cellSize = 1;
        int splitIterations = 1;

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cell size must be greater than 1")
                .hasNoCause();
    }

    @Test
    void shouldValidateIllegalCellSizeWhenModified() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setCellSize(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cell size must be greater than 1")
                .hasNoCause();
    }

    @Test
    void shouldValidateZeroRootRangeWhenConstructed() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        BigDecimal rootRange = BigDecimal.ZERO;

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .rootRange(rootRange)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateZeroRootRangeWhenModified() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setRootRange(BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateNegativeRootRangeWhenConstructed() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        BigDecimal rootRange = BigDecimal.ONE.negate();

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .rootRange(rootRange)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateNegativeRootRangeWhenModified() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setRootRange(BigDecimal.ONE.negate()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be greater than 0")
                .hasNoCause();
    }

    @Test
    void shouldValidateTooBigRootRangeWhenConstructed() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        BigDecimal rootRange = BigDecimal.valueOf(Double.MAX_VALUE / 2)
                .add(BigDecimal.valueOf(Double.MIN_VALUE));

        // when / then
        assertThatThrownBy(() -> DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .rootRange(rootRange)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be lower than Integer.MAX_VALUE")
                .hasNoCause();
    }

    @Test
    void shouldValidateTooBigRootRangeWhenModified() {
        // given
        int cellSize = 2;
        int splitIterations = 1;
        BigDecimal rootRange = BigDecimal.valueOf(Double.MAX_VALUE / 2)
                .add(BigDecimal.valueOf(Double.MIN_VALUE));

        DataMatrixCellConfiguration configuration = DataMatrixCellConfiguration.builder()
                .cellSize(cellSize)
                .splitIterations(splitIterations)
                .build();

        // when / then
        assertThatThrownBy(() -> configuration.setRootRange(rootRange))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Root range must be lower than Integer.MAX_VALUE")
                .hasNoCause();
    }
}
