package com.h8.nh.nhoodengine.example.worldcities.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.example.utils.GenericDataLoader;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;

import java.util.HashMap;
import java.util.Map;

public final class WorldCityDataLoader extends GenericDataLoader<WorldCityMetadata, WorldCity> {

    private static final String WORLD_CITIES_RESOURCE_FILE = "worldcities/worldcities.csv";
    private static final String WORLD_CITIES_RESOURCE_FILE_DELIMITER = ",";

    private static final String ROW_HEADER_CITY_NAME = "CITY";
    private static final String ROW_HEADER_CITY_LATITUDE = "LAT";
    private static final String ROW_HEADER_CITY_LONGITUDE = "LNG";
    private static final String ROW_HEADER_COUNTRY_NAME = "COUNTRY";

    private int latitudeIdx;
    private int longitudeIdx;
    private int cityNameIdx;
    private int countryNameIdx;

    public WorldCityDataLoader(
            final DataMatrixRepository<WorldCityMetadata, WorldCity> repository) {
        super(repository);
    }

    @Override
    protected String getResourceFile() {
        return WORLD_CITIES_RESOURCE_FILE;
    }

    @Override
    protected String getResourceFileDelimiter() {
        return WORLD_CITIES_RESOURCE_FILE_DELIMITER;
    }

    @Override
    protected void mapHeaders(final String[] headersRow) {
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

    @Override
    protected DataResource<WorldCityMetadata, WorldCity> mapRow(final String[] row) {
        double latitude = mapDoubleValue(row[latitudeIdx]);
        double longitude = mapDoubleValue(row[longitudeIdx]);

        String cityName = mapStringValue(row[cityNameIdx]);
        String countryName = mapStringValue(row[countryNameIdx]);

        return DataResource.<WorldCityMetadata, WorldCity>builder()
                        .key(WorldCityMetadata.of(latitude, longitude))
                        .data(WorldCity.of(cityName, countryName))
                        .build();
    }
}
