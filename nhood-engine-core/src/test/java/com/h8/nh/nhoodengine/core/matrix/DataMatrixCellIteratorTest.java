package com.h8.nh.nhoodengine.core.matrix;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataMatrixCellIteratorTest {

    private final DataMatrixCellConfiguration cellConfiguration =
            DataMatrixCellConfiguration.builder()
                    .splitIterations(2)
                    .cellSize(2)
                    .build();

    @Test
    void shouldReturnCellWhenIteratingThroughResourceCell() {
        // given
        DataMatrixCell<DataMatrixResource> cell =
                DataMatrixCell.root(3, cellConfiguration);
        cell.add(() -> new double[]{0.0, 0.0, 0.0});

        assertThat(cell.hasChildren()).isFalse();
        assertThat(cell.hasResources()).isTrue();

        // when
        double[] entryPoint = new double[]{50.0, 50.0, 50.0};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(0.0)).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell).isEqualTo(cell);

        assertThat(iterator.hasNext()).isFalse();
        assertThat(iterator.hasNextWithinRange(0.0)).isFalse();
    }

    @Test
    void shouldReturnFirstResourceCellWhenIteratingThroughSplitCell() {
        // given
        DataMatrixCell<DataMatrixResource> cell =
                DataMatrixCell.root(3, cellConfiguration);
        cell.add(() -> new double[]{-50.0, -50.0, -50.0});
        cell.add(() -> new double[]{50.0, 50.0, 50.0});

        assertThat(cell.hasChildren()).isTrue();
        assertThat(cell.hasResources()).isFalse();

        // when
        double[] entryPoint = new double[]{50.0, 50.0, 50.0};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(0.0)).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell).isNotEqualTo(cell);
        assertThat(actualCell.getResources()).hasSize(1);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(0.0)).isFalse();
        assertThat(iterator.hasNextWithinRange(100.0)).isTrue();
    }

    @Test
    void shouldReturnExpectedSequenceOfCells() {
        // given
        DataMatrixResource r1 = () -> new double[]{50.0, 50.0, 50.0};
        DataMatrixResource r2 = () -> new double[]{100.0, 100.0, 100.0};
        DataMatrixResource r3 = () -> new double[]{200.0, 200.0, 200.0};

        DataMatrixCell<DataMatrixResource> cell =
                  DataMatrixCell.root(3, cellConfiguration);
        cell.add(r1);
        cell.add(r2);
        cell.add(r3);

        // when
        double[] entryPoint = new double[]{50.0, 50.0, 50.0};
        DataMatrixCellIterator<DataMatrixResource> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell);

        // then
        assertThat(iterator.hasNext()).isTrue();
        DataMatrixCell<DataMatrixResource> actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r1);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r2);

        assertThat(iterator.hasNext()).isTrue();
        actualCell = iterator.next();
        assertThat(actualCell.getResources()).containsExactlyInAnyOrder(r3);

        assertThat(iterator.hasNext()).isFalse();
    }
}