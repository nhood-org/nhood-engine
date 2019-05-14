package com.h8.nh.nhoodengine.utils;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DataKeyGenerator {

    private DataKeyGenerator() {
    }

    public static Stream<Integer[]> generate(
            final Integer[] min, final Integer[] max) {
        validateLimitVectors(min, max);
        return generateKeysStream(min, max);
    }

    private static void validateLimitVectors(
            final Integer[] min, final Integer[] max) {
        if (min == null) {
            throw new IllegalArgumentException("Min limits vector must not be null");
        }

        if (min.length == 0) {
            throw new IllegalArgumentException("Min limits vector must not be empty");
        }

        if (max == null) {
            throw new IllegalArgumentException("Max limits vector must not be null");
        }

        if (max.length == 0) {
            throw new IllegalArgumentException("Max limits vector must not be empty");
        }

        if (min.length != max.length) {
            throw new IllegalArgumentException("Min and max limits vectors must have the same size");
        }
    }

    private static Stream<Integer[]> generateKeysStream(
            final Integer[] min, final Integer[] max) {
        Stream<Integer[]> keys = null;
        for (int i = 0; i < min.length; i++) {
            keys = generateNextLevelKeysStream(keys, min[i], max[i]);
        }
        return keys;
    }

    private static Stream<Integer[]> generateNextLevelKeysStream(
            final Stream<Integer[]> previousLevelKeys, final Integer min, final Integer max) {
        if (previousLevelKeys == null) {
            return IntStream
                    .range(min, max)
                    .mapToObj(i -> new Integer[] {i});
        } else {
            return previousLevelKeys
                    .flatMap(v -> generateNextLevelKeysStream(v, min, max));
        }
    }

    private static Stream<Integer[]> generateNextLevelKeysStream(
            final Integer[] previousLevelKey, final Integer min, final Integer max) {
        return IntStream
                .range(min, max)
                .mapToObj(i -> generateNextLevelKey(previousLevelKey, i));
    }

    private static Integer[] generateNextLevelKey(
            final Integer[] previousLevelKey, final Integer nextValue) {
        Integer[] nextLevelKey = new Integer[previousLevelKey.length + 1];
        nextLevelKey[previousLevelKey.length] = nextValue;
        System.arraycopy(previousLevelKey, 0, nextLevelKey, 0, previousLevelKey.length);
        return nextLevelKey;
    }
}
