package com.h8.nh.nhoodengine.example.cities.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.Scanner;

public final class WorldCityDataLoader {

    private static final String WORLD_CITIES_RESOURCE_FILE
            = "simplemaps/worldcities/basic_1.5/worldcities.csv";

    private static final String WORLD_CITIES_DELIMITER
            = ",";

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
            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(WORLD_CITIES_DELIMITER);
                DataResource<WorldCityMetadata, WorldCity> resource = mapRow(row);
                repository.add(resource);
            }
        }
    }

    private DataResource<WorldCityMetadata, WorldCity> mapRow(final String[] row) {
        double latitude = mapDoubleValue(row[2]);
        double longitude = mapDoubleValue(row[3]);
        return DataResource.<WorldCityMetadata, WorldCity>builder()
                        .key(WorldCityMetadata.of(latitude, longitude))
                        .data(WorldCity.of(row[0], row[4]))
                        .build();
    }

    private double mapDoubleValue(final String value) {
        return Double.valueOf(value.replace("\"", ""));
    }
}
