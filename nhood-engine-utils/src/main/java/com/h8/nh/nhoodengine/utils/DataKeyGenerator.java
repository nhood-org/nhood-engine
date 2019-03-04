package com.h8.nh.nhoodengine.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class DataKeyGenerator {

    private DataKeyGenerator() {
    }

    public static Stream<Vector<Integer>> generate(
            final Vector<Integer> min, final Vector<Integer> max) {
        validateLimitVectors(min, max);
        return generateKeysStream(min, max);
    }

    private static void validateLimitVectors(
            final Vector<Integer> min, final Vector<Integer> max) {
        if (min == null) {
            throw new IllegalArgumentException("Min limits vector must not be null");
        }

        if (min.isEmpty()) {
            throw new IllegalArgumentException("Min limits vector must not be empty");
        }

        if (max == null) {
            throw new IllegalArgumentException("Max limits vector must not be null");
        }

        if (max.isEmpty()) {
            throw new IllegalArgumentException("Max limits vector must not be empty");
        }

        if (min.size() != max.size()) {
            throw new IllegalArgumentException("Min and max limits vectors must have the same size");
        }
    }

    private static Stream<Vector<Integer>> generateKeysStream(
            final Vector<Integer> min, final Vector<Integer> max) {
        Stream<Vector<Integer>> keys = null;
        for (int i = 0; i < min.size(); i++) {
            keys = generateNextLevelKeysStream(keys, min.get(i), max.get(i));
        }
        return keys;
    }

    private static Stream<Vector<Integer>> generateNextLevelKeysStream(
            final Stream<Vector<Integer>> previousLevelKeys, final Integer min, final Integer max) {
        if (previousLevelKeys == null) {
            return IntStream
                    .range(min, max)
                    .mapToObj(Vector::new);
        } else {
            return previousLevelKeys
                    .flatMap(v -> generateNextLevelKeysStream(v, min, max));
        }
    }

    private static Stream<Vector<Integer>> generateNextLevelKeysStream(
            final Vector<Integer> previousLevelKey, final Integer min, final Integer max) {
        return IntStream
                .range(min, max)
                .mapToObj(i -> generateNextLevelKey(previousLevelKey, i));
    }

    private static Vector<Integer> generateNextLevelKey(
            final Vector<Integer> previousLevelKey, final Integer nextValue) {
        List<Integer> nextLevelKey = new ArrayList<>(previousLevelKey);
        nextLevelKey.add(nextValue);
        return new Vector<>(nextLevelKey);
    }
}
