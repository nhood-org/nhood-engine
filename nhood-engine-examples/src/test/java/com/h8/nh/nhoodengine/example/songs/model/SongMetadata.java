package com.h8.nh.nhoodengine.example.songs.model;

import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;
import java.util.Arrays;

public final class SongMetadata implements DataResourceKey {

    public static final int METADATA_SIZE = 10;
    private static final int COORDINATE_MULTIPLICAND = 1;

    private final double[] metadata;

    private SongMetadata(final double[] metadata) {
        if (metadata.length != METADATA_SIZE) {
            String message = String.format("illegal metadata size %d (required %d)", metadata.length, METADATA_SIZE);
            throw new IllegalArgumentException(message);
        }
        this.metadata = metadata;
    }

    public static SongMetadata of(final double[] metadata) {
        return new SongMetadata(metadata);
    }

    @Override
    public BigDecimal[] unified() {
        BigDecimal[] m = new BigDecimal[METADATA_SIZE];
        m[0] = BigDecimal.valueOf(metadata[0] * COORDINATE_MULTIPLICAND);
        m[1] = BigDecimal.valueOf(metadata[1] * COORDINATE_MULTIPLICAND);
        m[2] = BigDecimal.valueOf(metadata[2] * COORDINATE_MULTIPLICAND);
        m[3] = BigDecimal.valueOf(metadata[3] * COORDINATE_MULTIPLICAND);
        m[4] = BigDecimal.valueOf(metadata[4] * COORDINATE_MULTIPLICAND);
        m[5] = BigDecimal.valueOf(metadata[5] * COORDINATE_MULTIPLICAND);
        m[6] = BigDecimal.valueOf(metadata[6] * COORDINATE_MULTIPLICAND);
        m[7] = BigDecimal.valueOf(metadata[7] * COORDINATE_MULTIPLICAND);
        m[8] = BigDecimal.valueOf(metadata[8] * COORDINATE_MULTIPLICAND);
        m[9] = BigDecimal.valueOf(metadata[9] * COORDINATE_MULTIPLICAND);
        return m;
    }

    public boolean isZeroVector() {
        return Arrays.stream(metadata).allMatch(d -> d == 0.0d);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SongMetadata that = (SongMetadata) o;
        return Arrays.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(metadata);
    }
}
