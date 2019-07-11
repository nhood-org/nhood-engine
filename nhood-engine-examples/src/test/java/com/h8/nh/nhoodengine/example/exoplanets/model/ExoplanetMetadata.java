package com.h8.nh.nhoodengine.example.exoplanets.model;

import com.h8.nh.nhoodengine.core.DataResourceKey;

import java.math.BigDecimal;
import java.util.Objects;

public final class ExoplanetMetadata implements DataResourceKey {

    public static final int METADATA_SIZE = 11;
    private static final int COORDINATE_MULTIPLICAND = 1000;

    /**
     * P_MASS_EST - planet mass estimated from mass-radius relation (earth units)
     */
    private final double mass;
    private static final double MIN_MASS = 0.019;
    private static final double MAX_MASS = 18000;

    /**
     * P_RADIUS_EST - planet radius estimated from mass-radius relation (earth units)
     */
    private final double radius;
    private static final double MIN_RADIUS = 0;
    private static final double MAX_RADIUS = 77.3;

    /**
     * P_PERIOD - planet period (days)
     */
    private final double period;
    private static final double MIN_PERIOD = 0.091;
    private static final double MAX_PERIOD = 7300000;

    /**
     * P_TEMP_EQUIL - planet equilibrium temperature assuming bond albedo 0.3 (K)
     */
    private final double temperature;
    private static final double MIN_TEMP = 2.688;
    private static final double MAX_TEMP = 7056.775;

    /**
     * P_PERIASTRON - planet periastron (AU)
     */
    private final double periastron;
    private static final double MIN_PERIASTRON = 0.004;
    private static final double MAX_PERIASTRON = 2500;

    /**
     * P_APASTRON - planet apastron (AU)
     */
    private final double apastron;
    private static final double MIN_APASTRON = 0.004;
    private static final double MAX_APASTRON = 2500;

    /**
     * P_FLUX - planet mean stellar flux (earth units)
     */
    private final double flux;
    private static final double MIN_FLUX = 0;
    private static final double MAX_FLUX = 588634.37;

    /**
     * S_MASS - star mass (solar units)
     */
    private final double starMass;
    private static final double MIN_STAR_MASS= 0.01;
    private static final double MAX_STAR_MASS = 23.56;

    /**
     * S_RADIUS - star radius (solar units)
     */
    private final double starRadius;
    private static final double MIN_STAR_RADIUS = 0.01;
    private static final double MAX_STAR_RADIUS = 71.23;


    /**
     * P_DISTANCE_EFF - planet effective thermal distance from the star (AU)
     */
    private final double starDistance;
    private static final double MIN_DISTANCE = 0.004;
    private static final double MAX_DISTANCE = 2500;

    /**
     * S_LUMINOSITY - star luminosity (solar units)
     */
    private final double starLuminosity;
    private static final double MIN_LUMINOSITY = 0;
    private static final double MAX_LUMINOSITY = 1486.895;

    private ExoplanetMetadata(
            final double mass,
            final double radius,
            final double period,
            final double temperature,
            final double periastron,
            final double apastron,
            final double flux,
            final double starMass,
            final double starRadius,
            final double starDistance,
            final double starLuminosity) {
        this.mass = mass;
        this.radius = radius;
        this.period = period;
        this.temperature = temperature;
        this.periastron = periastron;
        this.apastron = apastron;
        this.flux = flux;
        this.starMass = starMass;
        this.starRadius = starRadius;
        this.starDistance = starDistance;
        this.starLuminosity = starLuminosity;
    }

    public static ExoplanetMetadataBuilder builder() {
        return new ExoplanetMetadataBuilder();
    }

    @Override
    public BigDecimal[] unified() {
        BigDecimal[] m = new BigDecimal[METADATA_SIZE];
        m[0] = featureScaled(mass, MIN_MASS, MAX_MASS);
        m[1] = featureScaled(radius, MIN_RADIUS, MAX_RADIUS);
        m[2] = featureScaled(period, MIN_PERIOD, MAX_PERIOD);
        m[3] = featureScaled(temperature, MIN_TEMP, MAX_TEMP);
        m[4] = featureScaled(periastron, MIN_PERIASTRON, MAX_PERIASTRON);
        m[5] = featureScaled(apastron, MIN_APASTRON, MAX_APASTRON);
        m[6] = featureScaled(flux, MIN_FLUX, MAX_FLUX);
        m[7] = featureScaled(starMass, MIN_STAR_MASS, MAX_STAR_MASS);
        m[8] = featureScaled(starRadius, MIN_STAR_RADIUS, MAX_STAR_RADIUS);
        m[9] = featureScaled(starDistance, MIN_DISTANCE, MAX_DISTANCE);
        m[10] = featureScaled(starLuminosity, MIN_LUMINOSITY, MAX_LUMINOSITY);
        return m;
    }

    private BigDecimal featureScaled(
            final double value, final double min, final double max) {
        double scaled = (value - min) / (max - min);
        return BigDecimal.valueOf(scaled * COORDINATE_MULTIPLICAND);
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
                && Double.compare(exoplanet.starMass, starMass) == 0
                && Double.compare(exoplanet.starRadius, starRadius) == 0
                && Double.compare(exoplanet.starDistance, starDistance) == 0
                && Double.compare(exoplanet.starLuminosity, starLuminosity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                mass, radius, period, temperature, periastron, apastron,
                flux, starMass, starRadius, starDistance, starLuminosity);
    }


    public static final class ExoplanetMetadataBuilder {

        private double mass;
        private double radius;
        private double period;
        private double temperature;
        private double periastron;
        private double apastron;
        private double flux;
        private double starMass;
        private double starRadius;
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

        public ExoplanetMetadataBuilder starMass(double starMass) {
            this.starMass = starMass;
            return this;
        }

        public ExoplanetMetadataBuilder starRadius(double starRadius) {
            this.starRadius = starRadius;
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
                    mass, radius, period, temperature, periastron, apastron,
                    flux, starMass, starRadius, starDistance, starLuminosity);
        }
    }
}
