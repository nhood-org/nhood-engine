package com.h8.nh.nhoodengine.example.exoplanets.model;

import java.util.Objects;

public final class Exoplanet {

    private final String name;

    private Exoplanet(final String name) {
        this.name = name;
    }

    public static Exoplanet of(final String name) {
        return new Exoplanet(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Exoplanet exoplanet = (Exoplanet) o;
        return Objects.equals(name, exoplanet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
