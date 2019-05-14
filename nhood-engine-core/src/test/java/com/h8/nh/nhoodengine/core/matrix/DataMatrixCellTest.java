package com.h8.nh.nhoodengine.core.matrix;

import com.h8.nh.nhoodengine.core.DataResource;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.h8.nh.nhoodengine.core.utils.DataResourceUtils.resource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixCellTest {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal TEN = BigDecimal.TEN;
    private static final BigDecimal TEN_PLUS = BigDecimal.TEN.add(BigDecimal.valueOf(Double.MIN_VALUE));

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal HUNDRED_MINUS = HUNDRED.subtract(BigDecimal.valueOf(Double.MIN_VALUE));

    private static final Offset<BigDecimal> OFFSET = Offset.offset(BigDecimal.valueOf(0.0001));

    private final DataMatrixCellConfiguration cellConfiguration =
            DataMatrixCellConfiguration.builder()
                    .splitIterations(1)
                    .cellSize(2)
                    .build();

    @Test
    void shouldCreateCellWithGivenIndexAndDimensions() {
        // given
        BigDecimal[] cellIndex = new BigDecimal[]{ZERO, ZERO, ZERO};
        BigDecimal[] cellClosure = new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED};

        // when
        DataMatrixCell<DataResource> cell =
                new DataMatrixCell<>(cellIndex, cellClosure, null, cellConfiguration);

        // then
        assertThat(cell.getIndex()).isEqualTo(cellIndex);
        assertThat(cell.getClosure()).isEqualTo(cellClosure);
    }

    @Test
    void shouldNotCreateCellWithIllegalIndexAndDimensions() {
        // given
        BigDecimal[] cellIndex = new BigDecimal[]{ZERO, ZERO};
        BigDecimal[] cellClosure = new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED};

        // when / then
        assertThatThrownBy(() -> new DataMatrixCell<>(cellIndex, cellClosure, null, cellConfiguration))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Index and closure arrays must have the same length")
                .hasNoCause();
    }

    @Test
    void shouldAcceptResourceWhenAddingToRelevantCell() {
        // given
        DataResource r = resource(() -> new BigDecimal[]{ZERO, ZERO, ZERO});
        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).containsOnly(r);
    }

    @Test
    void shouldAcceptResourceWhenAddingToSplitCell() {
        // given
        DataResource r1 = resource(() -> new BigDecimal[]{ZERO, ZERO, ZERO});
        DataResource r2 = resource(() -> new BigDecimal[]{ONE, ONE, ONE});
        DataResource r3 = resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        cell.add(r1);
        cell.add(r2);
        cell.add(r3);

        // then
        assertThat(cell.getResources()).isEmpty();
        int size = (int) Math.pow(2, cellConfiguration.getSplitIterations());
        assertThat(cell.getChildren()).hasSize(size);
    }

    @Test
    void shouldNotAcceptResourceWhenAddingToIrrelevantCell() {
        // given
        DataResource r = resource(() -> new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED});

        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when / then
        assertThatThrownBy(() -> cell.add(r))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cell does not cover given key")
                .hasNoCause();
    }

    @Test
    void shouldSplitCellWhenLimitOfResourcesIsExceeded() {
        // given
        DataResource r1 = resource(() -> new BigDecimal[]{ZERO, ZERO, ZERO});
        DataResource r2 = resource(() -> new BigDecimal[]{TEN, TEN, TEN});

        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        cell.add(r1);
        cell.add(r2);

        // then
        assertThat(cell.getResources()).isEmpty();
        int size = (int) Math.pow(2, cellConfiguration.getSplitIterations());
        assertThat(cell.getChildren()).hasSize(size);

        DataMatrixCell<DataResource> r1Cell = cell.getChildren().iterator().next();
        assertThat(r1Cell.getResources()).hasSize(1);
        assertThat(r1Cell.getResources()).containsAnyOf(r1, r2);
        assertThat(r1Cell.getParent()).isEqualTo(cell);

        DataMatrixCell<DataResource> r2Cell = cell.getChildren().iterator().next();
        assertThat(r2Cell.getResources()).hasSize(1);
        assertThat(r2Cell.getResources()).containsAnyOf(r1, r2);
        assertThat(r2Cell.getParent()).isEqualTo(cell);
    }

    @Test
    void shouldNotSplitCellWhenNumberOfResourcesIsBelowLimit() {
        // given
        DataResource r = resource(() -> new BigDecimal[]{ZERO, ZERO, ZERO});

        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).containsOnly(r);
        assertThat(cell.getChildren()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("pointsAndDistances")
    void shouldReturnProperDistanceFromGivenPoint(
            final BigDecimal[] point,
            final BigDecimal expectedDistance) {
        // given
        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        BigDecimal actualDistance = cell.distanceFrom(point);

        // then
        assertThat(actualDistance)
                .isCloseTo(expectedDistance, OFFSET);
    }

    private static Stream<Arguments> pointsAndDistances() {
        return Stream.of(
                Arguments.of(new BigDecimal[]{ZERO, ZERO, ZERO}, ZERO),
                Arguments.of(new BigDecimal[]{TEN, TEN, TEN}, ZERO),
                Arguments.of(new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED}, ZERO),

                Arguments.of(new BigDecimal[]{HUNDRED.add(ONE), ZERO, ZERO}, ONE),
                Arguments.of(new BigDecimal[]{ZERO, HUNDRED.add(TEN), ZERO}, TEN),
                Arguments.of(new BigDecimal[]{ZERO, ZERO, HUNDRED.add(HUNDRED)}, HUNDRED),

                Arguments.of(new BigDecimal[]{ONE.negate(), ZERO, ZERO}, ONE),
                Arguments.of(new BigDecimal[]{ZERO, TEN.negate(), ZERO}, TEN),
                Arguments.of(new BigDecimal[]{ZERO, ZERO, HUNDRED.negate()}, HUNDRED),

                Arguments.of(new BigDecimal[]{TEN.negate(), TEN.negate(), ZERO},
                        BigDecimal.valueOf(10.0 * Math.sqrt(2.0))),
                Arguments.of(new BigDecimal[]{HUNDRED.add(TEN), HUNDRED.add(TEN), HUNDRED.add(TEN)},
                        BigDecimal.valueOf(10.0 * Math.sqrt(3.0)))
        );
    }

    @ParameterizedTest
    @MethodSource("pointsAndWrapResults")
    void shouldReturnWrapResultForGivenPoints(
            final BigDecimal[] point,
            final boolean expectedResult) {
        // given
        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        boolean actualResult = cell.wrapsKey(point);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> pointsAndWrapResults() {
        return Stream.of(
                Arguments.of(new BigDecimal[]{ZERO, ZERO, ZERO}, true),
                Arguments.of(new BigDecimal[]{TEN, TEN, TEN}, true),
                Arguments.of(new BigDecimal[]{HUNDRED_MINUS, HUNDRED_MINUS, HUNDRED_MINUS}, true),
                Arguments.of(new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED}, false),

                Arguments.of(new BigDecimal[]{HUNDRED.add(ONE), ZERO, ZERO}, false),
                Arguments.of(new BigDecimal[]{ZERO, HUNDRED.add(TEN), ZERO}, false),
                Arguments.of(new BigDecimal[]{ZERO, ZERO, HUNDRED.add(HUNDRED)}, false),

                Arguments.of(new BigDecimal[]{ONE.negate(), ZERO, ZERO}, false),
                Arguments.of(new BigDecimal[]{ZERO, TEN.negate(), ZERO}, false),
                Arguments.of(new BigDecimal[]{ZERO, ZERO, HUNDRED.negate()}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("pointsAndRangesAndWrapResults")
    void shouldReturnWrapResultForGivenPointsAndRanges(
            final BigDecimal[] point,
            final BigDecimal range,
            final boolean expectedResult) {
        // given
        DataMatrixCell<DataResource> cell = new DataMatrixCell<>(
                new BigDecimal[]{ZERO, ZERO, ZERO},
                new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED},
                null,
                cellConfiguration);

        // when
        boolean actualResult = cell.wrapsKey(point, range);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> pointsAndRangesAndWrapResults() {
        return Stream.of(
                Arguments.of(new BigDecimal[]{ZERO, ZERO, ZERO}, ZERO, true),
                Arguments.of(new BigDecimal[]{TEN, TEN, TEN}, ZERO, true),
                Arguments.of(new BigDecimal[]{HUNDRED_MINUS, HUNDRED_MINUS, HUNDRED_MINUS}, ZERO, true),
                Arguments.of(new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED}, ZERO, false),

                Arguments.of(new BigDecimal[]{ZERO, ZERO, ZERO}, TEN, false),
                Arguments.of(new BigDecimal[]{TEN, TEN, TEN}, TEN, true),
                Arguments.of(new BigDecimal[]{TEN, TEN, TEN}, TEN_PLUS, false),
                Arguments.of(new BigDecimal[]{HUNDRED, HUNDRED, HUNDRED}, TEN, false)
        );
    }
}
