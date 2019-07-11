package com.h8.nh.nhoodengine.example.worldcities.model;

import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;
import java.util.Objects;

public final class WorldCityMetadata implements DataResourceKey {

    public static final int METADATA_SIZE = 2;
    private static final int COORDINATE_MULTIPLICAND = 1000;

    private final double latitude;
    private final double longitude;

    private WorldCityMetadata(
            final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static WorldCityMetadata of(
            final double latitude, final double longitude) {
        return new WorldCityMetadata(latitude, longitude);
    }

    @Override
    public BigDecimal[] unified() {
        BigDecimal[] m = new BigDecimal[METADATA_SIZE];
        m[0] = BigDecimal.valueOf(latitude * COORDINATE_MULTIPLICAND);
        m[1] = BigDecimal.valueOf(longitude * COORDINATE_MULTIPLICAND);
        return m;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorldCityMetadata that = (WorldCityMetadata) o;
        return Double.compare(that.latitude, latitude) == 0
                && Double.compare(that.longitude, longitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }
}
