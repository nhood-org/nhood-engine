package com.h8.nh.nhoodengine.example.worldcities.model;

import java.util.Objects;

public final class WorldCity {

    private final String name;
    private final String country;

    private WorldCity(
            final String name, final String country) {
        this.name = name;
        this.country = country;
    }

    public static WorldCity of(
            final String name, final String country) {
        return new WorldCity(name, country);
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorldCity worldCity = (WorldCity) o;
        return Objects.equals(name, worldCity.name)
                && Objects.equals(country, worldCity.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, country);
    }
}
