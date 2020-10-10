package com.h8.nh.nhoodengine.matrix.impl;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.core.DataResourceKey;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.h8.nh.nhoodengine.matrix.utils.DataResourceUtils.resource;
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
        DataResource<DataResourceKey, Object> r =
                resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataMatrixCellResource<DataResourceKey>> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        Map<UUID, Object> data = new HashMap<>();

        cell.add(DataMatrixCellResource.form(r));
        data.put(r.getUuid(), r.getData());

        assertThat(cell.hasChildren()).isFalse();
        assertThat(cell.hasResources()).isTrue();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResourceKey, Object> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell, data);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();

        Set<DataMatrixCellResource<DataResourceKey>> resourceSet = iterator.next()
                .stream()
                .map(DataMatrixCellResource::form)
                .collect(Collectors.toSet());
        assertThat(resourceSet).isEqualTo(cell.getResources());

        assertThat(iterator.hasNext()).isFalse();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
    }

    @Test
    void shouldReturnFirstResourceCellWhenIteratingThroughSplitCell() {
        // given
        DataResource<DataResourceKey, Object> r1 =
                resource(() -> new BigDecimal[]{TEN.negate(), TEN, TEN});
        DataResource<DataResourceKey, Object> r2 =
                resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataMatrixCellResource<DataResourceKey>> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        Map<UUID, Object> data = new HashMap<>();

        cell.add(DataMatrixCellResource.form(r1));
        data.put(r1.getUuid(), r1.getData());
        cell.add(DataMatrixCellResource.form(r2));
        data.put(r2.getUuid(), r2.getData());

        assertThat(cell.hasChildren()).isTrue();
        assertThat(cell.hasResources()).isFalse();

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResourceKey, Object> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell, data);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isTrue();

        Set<DataMatrixCellResource<DataResourceKey>> resourceSet = iterator.next()
                .stream()
                .map(DataMatrixCellResource::form)
                .collect(Collectors.toSet());
        assertThat(resourceSet).isNotEqualTo(cell.getResources());
        assertThat(resourceSet).hasSize(1);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNextWithinRange(ZERO)).isFalse();
        assertThat(iterator.hasNextWithinRange(HUNDRED)).isTrue();
    }

    @Test
    void shouldReturnExpectedSequenceOfCells() {
        // given
        DataResource<DataResourceKey, Object> r1 =
                resource(() -> new BigDecimal[]{ONE, ONE, ONE});
        DataResource<DataResourceKey, Object> r2 =
                resource(() -> new BigDecimal[]{TEN, TEN, TEN});
        DataResource<DataResourceKey, Object> r3 =
                resource(() -> new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED});
        DataResource<DataResourceKey, Object> r4 =
                resource(() -> new BigDecimal[]{TEN.negate(), TEN.negate(), TEN.negate()});
        DataResource<DataResourceKey, Object> r5 =
                resource(() -> new BigDecimal[]{HUNDRED.negate(), HUNDRED.negate(), HUNDRED.negate()});

        DataMatrixCell<DataMatrixCellResource<DataResourceKey>> cell =
                DataMatrixCellFactory.root(3, cellConfiguration);
        Map<UUID, Object> data = new HashMap<>();

        cell.add(DataMatrixCellResource.form(r1));
        data.put(r1.getUuid(), r1.getData());
        cell.add(DataMatrixCellResource.form(r2));
        data.put(r2.getUuid(), r2.getData());
        cell.add(DataMatrixCellResource.form(r3));
        data.put(r3.getUuid(), r3.getData());
        cell.add(DataMatrixCellResource.form(r4));
        data.put(r4.getUuid(), r4.getData());
        cell.add(DataMatrixCellResource.form(r5));
        data.put(r5.getUuid(), r5.getData());

        // when
        BigDecimal[] entryPoint = new BigDecimal[]{TEN, TEN, TEN};
        DataMatrixCellIterator<DataResourceKey, Object> iterator =
                DataMatrixCellIterator.startWith(entryPoint, cell, data);

        // then
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r2);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r3);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r1);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r4);

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).containsExactlyInAnyOrder(r5);

        assertThat(iterator.hasNext()).isFalse();
    }
}