package com.h8.nh.nhoodengine.core.matrix;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DataMatrixCellIteratorTest {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TEN = BigDecimal.TEN;
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final DataMatrixCellConfiguration cellConfiguration =
            DataMatrixCellConfiguration.builder()
                    .splitIterations(2)
                    .cellSize(2)
                    .build();

    @Test
    void shouldReturnCellWhenIteratingThroughResourceCell() {
        // given
        DataMatrixResource r = () -> new BigDecimal[]{TEN, TEN, TEN};

        DataMatrixCell<DataMatrixResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r);

        assertThat(cell.hasChildren()).isFalse();
        assertThat(cell.hasResources()).isTrue();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell).isEqualTo(cell);

        assertThat(iterator.hasNext()).isFalse();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
    }

    @Test
    void shouldReturnFirstResourceCellWhenIteratingThroughSplitCell() {
        // given
        DataMatrixResource r1 = () -> new BigDecimal[]{TEN.negate(), TEN, TEN};
        DataMatrixResource r2 = () -> new BigDecimal[]{TEN, TEN, TEN};

        DataMatrixCell<DataMatrixResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r1);
        cell.add(r2);

        assertThat(cell.hasChildren()).isTrue();
        assertThat(cell.hasResources()).isFalse();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell).isNotEqualTo(cell);
        assertThat(actualCell.getResources()).hasSize(1);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
        assertThat(iterator.hasNextWithinRange(HUNDRED)).isTrue();
    }

    @Test
    void shouldReturnExpectedSequenceOfCells() {
        // given
        DataMatrixResource r1 = () -> new BigDecimal[]{ONE, ONE, ONE};
        DataMatrixResource r2 = () -> new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixResource r3 = () -> new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED};
        DataMatrixResource r4 = () -> new BigDecimal[]{TEN.negate(), TEN.negate(), TEN.negate()};
        DataMatrixResource r5 = () -> new BigDecimal[]{HUNDRED.negate(), HUNDRED.negate(), HUNDRED.negate()};

        DataMatrixCell<DataMatrixResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r1);
        cell.add(r2);
        cell.add(r3);
        cell.add(r4);
        cell.add(r5);

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r2);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r3);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r1);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r4);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r5);

        assertThat(iterator.hasNext()).isFalse();
    }
}