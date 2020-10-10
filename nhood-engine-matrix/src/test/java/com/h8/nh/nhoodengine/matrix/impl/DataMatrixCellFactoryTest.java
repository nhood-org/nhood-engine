package com.h8.nh.nhoodengine.matrix.impl;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DataMatrixCellFactoryTest {

    private static final Offset<BigDecimal> OFFSET = Offset.offset(BigDecimal.valueOf(0.0001));

    @Test
    void shouldCreateRootCellWithGivenMetadataSize() {
        // given
        int metadataSize = 3;
        DataMatrixCellConfiguration cellConfiguration =
                DataMatrixCellConfiguration.builder()
                        .splitIterations(2)
                        .cellSize(100)
                        .rootRange(BigDecimal.TEN)
                        .build();

        // when
        DataMatrixCell<DataMatrixCellResource<?>> cell = DataMatrixCellFactory.root(metadataSize, cellConfiguration);

        // then
        assertThat(cell.getIndex()).hasSize(metadataSize);
        assertThat(cell.getIndex()[0]).isCloseTo(BigDecimal.valueOf(-5), OFFSET);

        assertThat(cell.getClosure()).hasSize(metadataSize);
        assertThat(cell.getClosure()[0]).isCloseTo(BigDecimal.valueOf(5), OFFSET);
    }
}
