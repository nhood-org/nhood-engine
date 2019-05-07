package com.h8.nh.nhoodengine.core.utils;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalUtilsTest {

    private static final Offset<BigDecimal> OFFSET = Offset.offset(BigDecimal.valueOf(0.0001));

    @Test
    void shouldReturnSquareRoot() {
        // given
        double random = new Random().nextDouble();

        // when
        BigDecimal expected = BigDecimal.valueOf(Math.sqrt(random));
        BigDecimal actualResult = BigDecimalUtils.sqrt(BigDecimal.valueOf(random));

        // then
        assertThat(actualResult).isCloseTo(expected, OFFSET);
    }

    @Test
    void shouldReturnZeroForZeroValue() {
        // when
        BigDecimal actualResult = BigDecimalUtils.sqrt(BigDecimal.ZERO);

        // then
        assertThat(actualResult).isEqualTo(BigDecimal.ZERO);
    }

}