package com.h8.nh.nhoodengine.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataKeyGeneratorTest {

    @ParameterizedTest
    @MethodSource("illegalLimitsArguments")
    void shouldThrowAnException(
            final Integer[] min,
            final Integer[] max,
            final String message) {
        assertThatThrownBy(() -> DataKeyGenerator.generate(min, max))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(message);
    }

    private static Stream<Arguments> illegalLimitsArguments() {
        return Stream.of(
                Arguments.of(null, vector(0),
                        "Min limits vector must not be null"),
                Arguments.of(vector(), vector(0),
                        "Min limits vector must not be empty"),
                Arguments.of(vector(0), null,
                        "Max limits vector must not be null"),
                Arguments.of(vector(0), vector(),
                        "Max limits vector must not be empty"),
                Arguments.of(vector(0), vector(0, 0),
                        "Min and max limits vectors must have the same size"));
    }

    @ParameterizedTest
    @MethodSource("properLimitsArguments")
    void shouldGenerateKeysInAccordanceWithGivenLimits(
            final Integer[] min,
            final Integer[] max,
            final List<Integer[]> expected) {
        List<Integer[]> actual = DataKeyGenerator.generate(min, max)
                .collect(Collectors.toList());
        assertThat(actual)
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    private static Stream<Arguments> properLimitsArguments() {
        return Stream.of(
                Arguments.of(vector(0), vector(1),
                        Collections.singletonList(vector(0))),
                Arguments.of(vector(0, 0), vector(1, 1),
                        Collections.singletonList(vector(0, 0))),
                Arguments.of(vector(-1), vector(1),
                        Arrays.asList(vector(-1), vector(0))),
                Arguments.of(vector(-1, -1), vector(1, 1),
                        Arrays.asList(vector(-1, -1), vector(0, -1), vector(-1, 0), vector(0, 0))),
                Arguments.of(vector(0, 0), vector(1, 3),
                        Arrays.asList(vector(0, 0), vector(0, 1), vector(0, 2))),
                Arguments.of(vector(0), vector(3),
                        Arrays.asList(vector(0), vector(1), vector(2))),
                Arguments.of(vector(0, 0), vector(2, 2),
                        Arrays.asList(vector(0, 0), vector(0, 1), vector(1, 0), vector(1, 1))));
    }

    private static Integer[] vector(final Integer... values) {
        return values;
    }
}
