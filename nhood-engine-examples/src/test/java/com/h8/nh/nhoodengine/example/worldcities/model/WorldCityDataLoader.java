package com.h8.nh.nhoodengine.example.worldcities.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class WorldCityDataLoader {

    private static final String WORLD_CITIES_RESOURCE_FILE = "worldcities/worldcities.csv";
    private static final String WORLD_CITIES_DELIMITER = ",";

    private static final String ROW_HEADER_CITY_NAME = "CITY";
    private static final String ROW_HEADER_CITY_LATITUDE = "LAT";
    private static final String ROW_HEADER_CITY_LONGITUDE = "LNG";
    private static final String ROW_HEADER_COUNTRY_NAME = "COUNTRY";

    private int latitudeIdx;
    private int longitudeIdx;
    private int cityNameIdx;
    private int countryNameIdx;

    private final DataMatrixRepository<WorldCityMetadata, WorldCity> repository;

    public WorldCityDataLoader(
            final DataMatrixRepository<WorldCityMetadata, WorldCity> repository) {
        this.repository = repository;
    }

    public void load()
            throws DataMatrixRepositoryFailedException {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(WORLD_CITIES_RESOURCE_FILE);

        if (in == null) {
            throw new IllegalStateException("Could not load resource file");
        }

        try(Scanner scanner = new Scanner(in)) {

            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Resource file is empty");
            }
            mapHeaders(scanner.nextLine().split(WORLD_CITIES_DELIMITER));

            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(WORLD_CITIES_DELIMITER);
                DataResource<WorldCityMetadata, WorldCity> resource = mapRow(row);
                repository.add(resource);
            }
        }
    }

    private void mapHeaders(final String[] headersRow) {
        Map<String, Integer> headerIndices = new HashMap<>();

        for (int i = 0; i < headersRow.length; i++) {
            String header = mapStringValue(headersRow[i]);
            headerIndices.put(header, i);
        }

        latitudeIdx = headerIndices.get(ROW_HEADER_CITY_LATITUDE);
        longitudeIdx = headerIndices.get(ROW_HEADER_CITY_LONGITUDE);
        cityNameIdx = headerIndices.get(ROW_HEADER_CITY_NAME);
        countryNameIdx = headerIndices.get(ROW_HEADER_COUNTRY_NAME);
    }

    private DataResource<WorldCityMetadata, WorldCity> mapRow(final String[] row) {
        double latitude = mapDoubleValue(row[latitudeIdx]);
        double longitude = mapDoubleValue(row[longitudeIdx]);

        String cityName = mapStringValue(row[cityNameIdx]);
        String countryName = mapStringValue(row[countryNameIdx]);

        return DataResource.<WorldCityMetadata, WorldCity>builder()
                        .key(WorldCityMetadata.of(latitude, longitude))
                        .data(WorldCity.of(cityName, countryName))
                        .build();
    }

    private double mapDoubleValue(final String value) {
        String s = mapStringValue(value);
        return Double.valueOf(s);
    }

    private String mapStringValue(final String value) {
        return value.replace("\"", "");
    }
}
