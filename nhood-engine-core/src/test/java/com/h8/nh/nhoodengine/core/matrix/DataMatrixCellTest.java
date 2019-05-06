package com.h8.nh.nhoodengine.core.matrix;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataMatrixCellTest {

    private final DataMatrixCellConfiguration cellConfiguration =
            DataMatrixCellConfiguration.builder()
                    .splitIterations(1)
                    .cellSize(2)
                    .build();

    @Test
    void shouldCreateRootCellWithGivenMetadataSize() {
        // given
        int metadataSize = 3;

        // when
        DataMatrixCell<DataMatrixResource> cell = DataMatrixCell.root(metadataSize, cellConfiguration);

        // then
        assertThat(cell.getIndex())
                .hasSize(metadataSize);
        assertThat(cell.getIndex())
                .containsOnly(
                        -1 * cellConfiguration.getRootRange() / 2,
                        -1 * cellConfiguration.getRootRange() / 2,
                        -1 * cellConfiguration.getRootRange() / 2);
        assertThat(cell.getDimensions())
                .hasSize(metadataSize);
        assertThat(cell.getDimensions())
                .containsOnly(
                        cellConfiguration.getRootRange(),
                        cellConfiguration.getRootRange(),
                        cellConfiguration.getRootRange());
    }

    @Test
    void shouldCreateCellWithGivenIndexAndDimensions() {
        // given
        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};

        // when
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

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
        assertThatThrownBy(() -> new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Index and dimensions arrays must have the same length")
                .hasNoCause();
    }

    @Test
    void shouldAcceptResourceWhenAddingToRelevantCell() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

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
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

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
        DataMatrixResource r = () -> new double[] {100.0, 100.0, 100.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

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
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

        // when
        cell.add(r1);
        cell.add(r2);

        // then
        assertThat(cell.getResources()).isEmpty();
        int size = (int) Math.pow(2, cellConfiguration.getSplitIterations());
        assertThat(cell.getChildren()).hasSize(size);

        DataMatrixCell<DataMatrixResource> r1Cell = cell.getChildren().iterator().next();
        assertThat(r1Cell.getResources()).hasSize(1);
        assertThat(r1Cell.getResources()).containsAnyOf(r1, r2);
        assertThat(r1Cell.getParent()).isEqualTo(cell);

        DataMatrixCell<DataMatrixResource> r2Cell = cell.getChildren().iterator().next();
        assertThat(r2Cell.getResources()).hasSize(1);
        assertThat(r2Cell.getResources()).containsAnyOf(r1, r2);
        assertThat(r2Cell.getParent()).isEqualTo(cell);
    }

    @Test
    void shouldNotSplitCellWhenNumberOfResourcesIsBelowLimit() {
        // given
        DataMatrixResource r = () -> new double[] {0.0, 0.0, 0.0};

        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

        // when
        cell.add(r);

        // then
        assertThat(cell.getResources()).containsOnly(r);
        assertThat(cell.getChildren()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("pointsAndDistances")
    void shouldReturnProperDistanceFromGivenPoint(
            final double[] point,
            final double expectedDistance) {
        // given
        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

        // when
        double actualDistance = cell.distanceFrom(point);

        // then
        assertThat(actualDistance).isCloseTo(expectedDistance, Offset.offset(0.0001));
    }

    private static Stream<Arguments> pointsAndDistances() {
        return Stream.of(
                Arguments.of(new double[] {0.0, 0.0, 0.0}, 0.0),
                Arguments.of(new double[] {50.0, 50.0, 50.0}, 0),
                Arguments.of(new double[] {100.0, 100.0, 100.0}, 0),
                Arguments.of(new double[] {-1.0, 0.0, 0.0}, 1.0),
                Arguments.of(new double[] {0.0, -10.0, 0.0}, 10.0),
                Arguments.of(new double[] {0.0, 0.0, -100.0}, 100.0),
                Arguments.of(new double[] {101.0, 0.0, 0.0}, 1.0),
                Arguments.of(new double[] {0.0, 110.0, 0.0}, 10.0),
                Arguments.of(new double[] {0.0, 0.0, 1110.0}, 1010.0),
                Arguments.of(new double[] {-10.0, -10.0, 0.0}, 10.0 * Math.sqrt(2.0)),
                Arguments.of(new double[] {110.0, 110.0, 110.0}, 10.0 * Math.sqrt(3.0))
        );
    }

    @ParameterizedTest
    @MethodSource("pointsAndWrapResults")
    void shouldReturnWrapResultForGivenPoints(
            final double[] point,
            final boolean expectedResult) {
        // given
        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

        // when
        boolean actualResult = cell.wrapsKey(point);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> pointsAndWrapResults() {
        return Stream.of(
                Arguments.of(new double[] {0.0, 0.0, 0.0}, true),
                Arguments.of(new double[] {50.0, 50.0, 50.0}, true),
                Arguments.of(new double[] {99.9, 99.9, 99.9}, true),
                Arguments.of(new double[] {100,0, 100, 100}, false),
                Arguments.of(new double[] {-1.0, 0.0, 0.0}, false),
                Arguments.of(new double[] {0.0, -10.0, 0.0}, false),
                Arguments.of(new double[] {0.0, 0.0, -100.0}, false),
                Arguments.of(new double[] {101.0, 0.0, 0.0}, false),
                Arguments.of(new double[] {0.0, 110.0, 0.0}, false),
                Arguments.of(new double[] {0.0, 0.0, 1110.0}, false),
                Arguments.of(new double[] {-10.0, -10.0, 0.0}, false),
                Arguments.of(new double[] {110.0, 110.0, 110.0}, false)
        );
    }

    @ParameterizedTest
    @MethodSource("pointsAndRangesAndWrapResults")
    void shouldReturnWrapResultForGivenPointsAndRanges(
            final double[] point,
            final double range,
            final boolean expectedResult) {
        // given
        double[] cellKey = new double[] {0.0, 0.0, 0.0};
        double[] cellDimensions = new double[] {100.0, 100.0, 100.0};
        DataMatrixCell<DataMatrixResource> cell =
                new DataMatrixCell<>(cellKey, cellDimensions, null, cellConfiguration);

        // when
        boolean actualResult = cell.wrapsKey(point, range);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> pointsAndRangesAndWrapResults() {
        return Stream.of(
                Arguments.of(new double[] {0.0, 0.0, 0.0}, 0.0, true),
                Arguments.of(new double[] {50.0, 50.0, 50.0}, 0.0, true),
                Arguments.of(new double[] {99.9, 99.9, 99.9}, 0.0, true),
                Arguments.of(new double[] {100.0, 100.0, 100.0}, 0.0, false),
                Arguments.of(new double[] {0.0, 0.0, 0.0}, 10.0, false),
                Arguments.of(new double[] {50.0, 50.0, 50.0}, 10.0, true),
                Arguments.of(new double[] {50.0, 50.0, 50.0}, 50.1, false),
                Arguments.of(new double[] {100.0, 100.0, 100.0}, 10.0, false)
        );
    }
}
