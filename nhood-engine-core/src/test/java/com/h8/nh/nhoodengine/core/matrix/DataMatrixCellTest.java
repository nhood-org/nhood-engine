package com.h8.nh.nhoodengine.core.matrix;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixCellTest {

    @Test
    void shouldAcceptResourceWhenAddingToApplicableCell() {
        // given
        double[] key = new double[] {0.0, 0.0, 0.0};
        DataMatrixResource r = () -> key;

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).contains(r);
    }

    @Test
    void shouldNotAcceptResourceWhenAddingToNotApplicableCell() {
        // given
        double[] key = new double[] {0.0, 0.0, 0.0};
        DataMatrixResource r = () -> key;

        double[] cellKey = new double[] {0.1, 0.1, 0.1};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell = new DataMatrixCell<>(cellKey, cellDimensions);

        // when / then
        assertThatThrownBy(() -> cell.add(r))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cell does not cover given key")
                .hasNoCause();
    }
}