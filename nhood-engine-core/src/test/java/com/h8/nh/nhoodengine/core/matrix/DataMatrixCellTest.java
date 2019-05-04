package com.h8.nh.nhoodengine.core.matrix;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixCellTest {

    private final DataMatrixCellConfiguration cellConfiguration =
            DataMatrixCellConfiguration.builder()
                    .splitFactor(2)
                    .cellSize(2)
                    .build();

    @Test
    void shouldCreateCellWithGivenMetadataSize() {
        // given
        int metadataSize = 3;

        // when
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(metadataSize, cellConfiguration);

        // then
        assertThat(cell.getIndex()).hasSize(metadataSize);
        assertThat(cell.getIndex()).containsOnly(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);
        assertThat(cell.getDimensions()).hasSize(metadataSize);
        assertThat(cell.getDimensions()).containsOnly(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    @Test
    void shouldCreateCellWithGivenIndexAndDimensions() {
        // given
        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};

        // when
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // then
        assertThat(cell.getIndex()).isEqualTo(cellKey);
        assertThat(cell.getDimensions()).isEqualTo(cellDimensions);
    }

    @Test
    void shouldNotCreateCellWithIllegalIndexAndDimensions() {
        // given
        double[] cellKey = new double[] {0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};

        // when / then
        assertThatThrownBy(() -> new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Index and dimensions arrays must have the same length")
                .hasNoCause();
    }

    @Test
    void shouldAcceptResourceWhenAddingToApplicableCell() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).containsOnly(r);
    }

    @Test
    void shouldAcceptResourceWhenAddingToSplitCell() {
        // given
        DataMatrixResource r1 = () -> new double[] {0.0, 0.0, 0.0};
        DataMatrixResource r2 = () -> new double[] {50.0, 50.0, 50.0};
        DataMatrixResource r3 = () -> new double[] {51.0, 51.0, 51.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // when
        cell.add(r1);
        cell.add(r2);
        cell.add(r3);

        // then
        assertThat(cell.getResources()).isEmpty();
        assertThat(cell.getChildren()).hasSize(cellConfiguration.getSplitFactor());
    }

    @Test
    void shouldNotAcceptResourceWhenAddingToNotApplicableCell() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.1, 0.1, 0.1};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // when / then
        assertThatThrownBy(() -> cell.add(r))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cell does not cover given key")
                .hasNoCause();
    }

    @Test
    void shouldSplitCellWhenLimitOfResourcesIsExceeded() {
        // given
        DataMatrixResource r1 = () -> new double[] {0.0, 0.0, 0.0};
        DataMatrixResource r2 = () -> new double[] {50.0, 50.0, 50.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // when
        cell.add(r1);
        cell.add(r2);

        // then
        assertThat(cell.getResources()).isEmpty();
        assertThat(cell.getChildren()).hasSize(cellConfiguration.getSplitFactor());

        DataMatrixCell<DataMatrixResource> r1Cell = cell.getChildren().iterator().next();
        assertThat(r1Cell.getResources()).hasSize(1);
        assertThat(r1Cell.getResources()).containsAnyOf(r1, r2);

        DataMatrixCell<DataMatrixResource> r2Cell = cell.getChildren().iterator().next();
        assertThat(r2Cell.getResources()).hasSize(1);
        assertThat(r2Cell.getResources()).containsAnyOf(r1, r2);
    }

    @Test
    void shouldNotSplitCellWhenLimitOfResourcesIsNotExceeded() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).containsOnly(r);
        assertThat(cell.getChildren()).isEmpty();
    }

    @Test
    void shouldResolveClosestCellFromSingleCell() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        cell.add(r);

        // when
        DataMatrixCell<DataMatrixResource> closestCell = cell.getClosestCell(() -> new double[] {10.0, 10.0, 10.0});

        // then
        assertThat(closestCell).isEqualTo(cell);
    }

    @Test
    void shouldResolveClosestCellFromSplitCell() {
        // given
        DataMatrixResource r1 = () -> new double[] {0.0, 0.0, 0.0};
        DataMatrixResource r2 = () -> new double[] {50.0, 50.0, 50.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        cell.add(r1);
        cell.add(r2);

        // when
        DataMatrixCell<DataMatrixResource> closestCell = cell.getClosestCell(() -> new double[] {10.0, 10.0, 10.0});

        // then
        assertThat(closestCell).isNotEqualTo(cell);
        assertThat(closestCell.getResources()).containsOnly(r1);
    }

    @Test
    void shouldThrowAnExceptionOnResolutionOfClosestCellFromNotApplicableOne() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions, cellConfiguration);

        cell.add(r);

        // when / then
        assertThatThrownBy(() -> cell.getClosestCell(() -> new double[] {100.1, 100.1, 100.1}))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cell does not cover given key")
                .hasNoCause();
    }
}
