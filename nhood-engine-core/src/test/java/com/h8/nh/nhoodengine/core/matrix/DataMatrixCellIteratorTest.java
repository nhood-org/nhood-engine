package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.h8.nh.nhoodengine.core.utils.DataResourceUtils.resource;
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
        DataResource r = resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r);

        assertThat(cell.hasChildren()).isFalse();
        assertThat(cell.hasResources()).isTrue();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();
        DataMatrixCell<DataResource> actualCell = iterator.next();
        assertThat(actualCell).isEqualTo(cell);

        assertThat(iterator.hasNext()).isFalse();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
    }

    @Test
    void shouldReturnFirstResourceCellWhenIteratingThroughSplitCell() {
        // given
        DataResource r1 = resource(() -> new BigDecimal[]{TEN.negate(), TEN, TEN});
        DataResource r2 = resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r1);
        cell.add(r2);

        assertThat(cell.hasChildren()).isTrue();
        assertThat(cell.hasResources()).isFalse();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();
        DataMatrixCell<DataResource> actualCell = iterator.next();
        assertThat(actualCell).isNotEqualTo(cell);
        assertThat(actualCell.getResources()).hasSize(1);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
        assertThat(iterator.hasNextWithinRange(HUNDRED)).isTrue();
    }

    @Test
    void shouldReturnExpectedSequenceOfCells() {
        // given
        DataResource r1 = resource(() -> new BigDecimal[]{ONE, ONE, ONE});
        DataResource r2 = resource(() -> new BigDecimal[]{TEN, TEN, TEN});
        DataResource r3 = resource(() -> new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED});
        DataResource r4 = resource(() -> new BigDecimal[]{TEN.negate(), TEN.negate(), TEN.negate()});
        DataResource r5 = resource(() -> new BigDecimal[]{HUNDRED.negate(), HUNDRED.negate(), HUNDRED.negate()});

        DataMatrixCell<DataResource> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        cell.add(r1);
        cell.add(r2);
        cell.add(r3);
        cell.add(r4);
        cell.add(r5);

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        DataMatrixCell<DataResource> actualCell = iterator.next();
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