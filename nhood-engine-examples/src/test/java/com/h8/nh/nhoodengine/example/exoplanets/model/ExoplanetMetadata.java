package com.h8.nh.nhoodengine.example.exoplanets.model;

import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExoplanetMetadata implements DataResourceKey {

    public static final int METADATA_SIZE = 9;

    /**
     * P_MASS - planet mass (earth masses)
     */
    private final double mass;

    /**
     * P_RADIUS_EST - planet radius estimated from mass-radius relation (earth units)
     */
    private final double radius;

    /**
     * P_PERIOD - planet period (days)
     */
    private final double period;

    /**
     * P_TEMP_EQUIL - planet equilibrium temperature assuming bond albedo 0.3 (K)
     */
    private final double temperature;

    /**
     * P_PERIASTRON - planet periastron (AU)
     */
    private final double periastron;

    /**
     * P_APASTRON - planet apastron (AU)
     */
    private final double apastron;

    /**
     * P_FLUX - planet mean stellar flux (earth units)
     */
    private final double flux;

    /**
     * P_DISTANCE_EFF - planet effective thermal distance from the star (AU)
     */
    private final double starDistance;

    /**
     * S_LUMINOSITY - star luminosity (solar units)
     */
    private final double starLuminosity;

    private ExoplanetMetadata(
            final double mass,
            final double radius,
            final double period,
            final double temperature,
            final double periastron,
            final double apastron,
            final double flux,
            final double starDistance,
            final double starLuminosity) {
        this.mass = mass;
        this.radius = radius;
        this.period = period;
        this.temperature = temperature;
        this.periastron = periastron;
        this.apastron = apastron;
        this.flux = flux;
        this.starDistance = starDistance;
        this.starLuminosity = starLuminosity;
    }

    public static ExoplanetMetadataBuilder builder() {
        return new ExoplanetMetadataBuilder();
    }

    @Override
    public BigDecimal[] unified() {
        BigDecimal[] m = new BigDecimal[METADATA_SIZE];
        m[0] = BigDecimal.valueOf(mass);
        m[1] = BigDecimal.valueOf(radius);
        m[2] = BigDecimal.valueOf(period);
        m[3] = BigDecimal.valueOf(temperature);
        m[4] = BigDecimal.valueOf(periastron);
        m[5] = BigDecimal.valueOf(apastron);
        m[6] = BigDecimal.valueOf(flux);
        m[7] = BigDecimal.valueOf(starDistance);
        m[8] = BigDecimal.valueOf(starLuminosity);
        return m;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExoplanetMetadata exoplanet = (ExoplanetMetadata) o;
        return Double.compare(exoplanet.mass, mass) == 0
                && Double.compare(exoplanet.radius, radius) == 0
                && Double.compare(exoplanet.period, period) == 0
                && Double.compare(exoplanet.temperature, temperature) == 0
                && Double.compare(exoplanet.periastron, periastron) == 0
                && Double.compare(exoplanet.apastron, apastron) == 0
                && Double.compare(exoplanet.flux, flux) == 0
                && Double.compare(exoplanet.starDistance, starDistance) == 0
                && Double.compare(exoplanet.starLuminosity, starLuminosity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                mass, radius, period, temperature, periastron, apastron, flux, starDistance, starLuminosity);
    }


    public static final class ExoplanetMetadataBuilder {

        private double mass;
        private double radius;
        private double period;
        private double temperature;
        private double periastron;
        private double apastron;
        private double flux;
        private double starDistance;
        private double starLuminosity;

        private ExoplanetMetadataBuilder() {
        }

        public ExoplanetMetadataBuilder mass(double mass) {
            this.mass = mass;
            return this;
        }

        public ExoplanetMetadataBuilder radius(double radius) {
            this.radius = radius;
            return this;
        }

        public ExoplanetMetadataBuilder period(double period) {
            this.period = period;
            return this;
        }

        public ExoplanetMetadataBuilder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public ExoplanetMetadataBuilder periastron(double periastron) {
            this.periastron = periastron;
            return this;
        }

        public ExoplanetMetadataBuilder apastron(double apastron) {
            this.apastron = apastron;
            return this;
        }

        public ExoplanetMetadataBuilder flux(double flux) {
            this.flux = flux;
            return this;
        }

        public ExoplanetMetadataBuilder starDistance(double starDistance) {
            this.starDistance = starDistance;
            return this;
        }

        public ExoplanetMetadataBuilder starLuminosity(double starLuminosity) {
            this.starLuminosity = starLuminosity;
            return this;
        }

        public ExoplanetMetadata build() {
            return new ExoplanetMetadata(
                    mass, radius, period, temperature, periastron, apastron, flux, starDistance, starLuminosity);
        }
    }
}
