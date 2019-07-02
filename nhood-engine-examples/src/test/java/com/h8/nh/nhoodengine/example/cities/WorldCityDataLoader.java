package com.h8.nh.nhoodengine.example.cities;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.Scanner;

final class WorldCityDataLoader {

    private static final String WORLD_CITIES_RESOURCE_FILE
            = "simplemaps_worldcities_basicv1.5/worldcities.csv";

    private static final String WORLD_CITIES_DELIMITER
            = ",";

    private final DataMatrixRepository<WorldCityMetadata, String> repository;

    WorldCityDataLoader(
            final DataMatrixRepository<WorldCityMetadata, String> repository) {
        this.repository = repository;
    }

    void load()
            throws DataMatrixRepositoryFailedException {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(WORLD_CITIES_RESOURCE_FILE);

        if (in == null) {
            throw new IllegalStateException("Could not load resource file");
        }

        try(Scanner scanner = new Scanner(in)) {
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(WORLD_CITIES_DELIMITER);
                DataResource<WorldCityMetadata, String> resource = mapRow(row);
                repository.add(resource);
            }
        }
    }

    private DataResource<WorldCityMetadata, String> mapRow(final String[] row) {
        String cityName = row[0];
        double latitude = mapDoubleValue(row[2]);
        double longitude = mapDoubleValue(row[3]);
        return DataResource.<WorldCityMetadata, String>builder()
                        .data(cityName)
                        .key(WorldCityMetadata.of(latitude, longitude))
                        .build();
    }

    private double mapDoubleValue(final String value) {
        return Double.valueOf(value.replace("\"", ""));
    }
}
