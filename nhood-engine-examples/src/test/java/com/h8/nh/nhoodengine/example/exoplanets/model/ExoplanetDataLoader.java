package com.h8.nh.nhoodengine.example.exoplanets.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.example.utils.GenericDataLoader;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;

import java.util.HashMap;
import java.util.Map;

public final class ExoplanetDataLoader extends GenericDataLoader<ExoplanetMetadata, Exoplanet> {

    private static final String EXOPLANET_RESOURCE_FILE = "exoplanets/phl_exoplanet_catalog.csv";
    private static final String EXOPLANET_RESOURCE_FILE_DELIMITER = ",";

    private static final String ROW_HEADER_PLANET_NAME = "P_NAME";
    private static final String ROW_HEADER_PLANET_MASS = "P_MASS_EST";
    private static final String ROW_HEADER_PLANET_RADIUS = "P_RADIUS_EST";
    private static final String ROW_HEADER_PLANET_PERIOD = "P_PERIOD";
    private static final String ROW_HEADER_PLANET_TEMP = "P_TEMP_EQUIL";
    private static final String ROW_HEADER_PLANET_PERIASTRON = "P_PERIASTRON";
    private static final String ROW_HEADER_PLANET_APASTRON = "P_APASTRON";
    private static final String ROW_HEADER_PLANET_FLUX = "P_FLUX";
    private static final String ROW_HEADER_STAR_MASS = "S_MASS";
    private static final String ROW_HEADER_STAR_RADIUS = "S_RADIUS";
    private static final String ROW_HEADER_STAR_DISTANCE = "P_DISTANCE_EFF";
    private static final String ROW_HEADER_STAR_LUMINOSITY = "S_LUMINOSITY";

    private int planetNameIdx;
    private int planetMassIdx;
    private int planetRadiusIdx;
    private int planetPeriodIdx;
    private int planetTempIdx;
    private int planetPeriastronIdx;
    private int planetApastronIdx;
    private int planetFluxIdx;
    private int starMassIdx;
    private int starRadiusIdx;
    private int starDistanceIdx;
    private int starLuminosityIdx;

    public ExoplanetDataLoader(
            final DataMatrixRepository<ExoplanetMetadata, Exoplanet> repository) {
        super(repository);
    }

    @Override
    protected String getResourceFile() {
        return EXOPLANET_RESOURCE_FILE;
    }

    @Override
    protected String getResourceFileDelimiter() {
        return EXOPLANET_RESOURCE_FILE_DELIMITER;
    }

    @Override
    protected void mapHeaders(final String[] headersRow) {
        Map<String, Integer> headerIndices = new HashMap<>();

        for (int i = 0; i < headersRow.length; i++) {
            String header = mapStringValue(headersRow[i]);
            headerIndices.put(header, i);
        }

        planetNameIdx = headerIndices.get(ROW_HEADER_PLANET_NAME);
        planetMassIdx = headerIndices.get(ROW_HEADER_PLANET_MASS);
        planetRadiusIdx = headerIndices.get(ROW_HEADER_PLANET_RADIUS);
        planetPeriodIdx = headerIndices.get(ROW_HEADER_PLANET_PERIOD);
        planetTempIdx = headerIndices.get(ROW_HEADER_PLANET_TEMP);
        planetPeriastronIdx = headerIndices.get(ROW_HEADER_PLANET_PERIASTRON);
        planetApastronIdx = headerIndices.get(ROW_HEADER_PLANET_APASTRON);
        planetFluxIdx = headerIndices.get(ROW_HEADER_PLANET_FLUX);
        starMassIdx = headerIndices.get(ROW_HEADER_STAR_MASS);
        starRadiusIdx = headerIndices.get(ROW_HEADER_STAR_RADIUS);
        starDistanceIdx = headerIndices.get(ROW_HEADER_STAR_DISTANCE);
        starLuminosityIdx = headerIndices.get(ROW_HEADER_STAR_LUMINOSITY);
    }

    @Override
    protected DataResource<ExoplanetMetadata, Exoplanet> mapRow(final String[] row) {
        ExoplanetMetadata m = ExoplanetMetadata.builder()
                .mass(mapDoubleValue(row[planetMassIdx]))
                .radius(mapDoubleValue(row[planetRadiusIdx]))
                .period(mapDoubleValue(row[planetPeriodIdx]))
                .temperature(mapDoubleValue(row[planetTempIdx]))
                .periastron(mapDoubleValue(row[planetPeriastronIdx]))
                .apastron(mapDoubleValue(row[planetApastronIdx]))
                .flux(mapDoubleValue(row[planetFluxIdx]))
                .starMass(mapDoubleValue(row[starMassIdx]))
                .starRadius(mapDoubleValue(row[starRadiusIdx]))
                .starDistance(mapDoubleValue(row[starDistanceIdx]))
                .starLuminosity(mapDoubleValue(row[starLuminosityIdx]))
                .build();

        String planetName = mapStringValue(row[planetNameIdx]);
        return DataResource.<ExoplanetMetadata, Exoplanet>builder()
                        .key(m)
                        .data(Exoplanet.of(planetName))
                        .build();
    }
}
