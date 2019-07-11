package com.h8.nh.nhoodengine.example.worldcities;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.impl.DataScoreComputationEngine;
import com.h8.nh.nhoodengine.example.worldcities.model.WorldCity;
import com.h8.nh.nhoodengine.example.worldcities.model.WorldCityDataLoader;
import com.h8.nh.nhoodengine.example.worldcities.model.WorldCityMetadata;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;
import com.h8.nh.nhoodengine.matrix.impl.DataMatrixCellBasedRepository;
import com.h8.nh.nhoodengine.utils.measurement.MeasurementChain;
import com.h8.nh.nhoodengine.utils.measurement.node.ExecutionTimeMeasurement;
import com.h8.nh.nhoodengine.utils.measurement.node.HeapMemoryMeasurement;
import org.junit.jupiter.api.Test;

/**
 * Example scenario:
 * <p>
 * There is a set of largest cities around the world loaded to the repository.
 * (database has been downloaded from simplemaps.com)
 * <p>
 * DataFinder is used to resolve 30 of cities
 * closest to Cracow considering its geographical coordinates.
 */
class WorldCitiesExampleTest {

    @Test
    void runExample()
            throws DataFinderFailedException, DataMatrixRepositoryFailedException {
        DataMatrixRepository<WorldCityMetadata, WorldCity> repository =
                new DataMatrixCellBasedRepository<>(WorldCityMetadata.METADATA_SIZE);
        new WorldCityDataLoader(repository).load();

        DataFinder<WorldCityMetadata, WorldCity> finder =
                new DataScoreComputationEngine<>(repository);

        int WORLD_CITIES_LIMIT = 30;
        double CRACOW_LATITUDE = 50.0600;
        double CRACOW_LONGITUDE = 19.9600;

        DataFinderCriteria<WorldCityMetadata> criteria =
                DataFinderCriteria.<WorldCityMetadata>builder()
                        .limit(WORLD_CITIES_LIMIT)
                        .metadata(WorldCityMetadata.of(CRACOW_LATITUDE, CRACOW_LONGITUDE))
                        .build();

        finder.find(criteria)
                .forEach(this::printResult);
    }

    @Test
    void runExampleWithMetrics() {
        Runnable example = () -> {
            try {
                runExample();
            } catch (DataFinderFailedException | DataMatrixRepositoryFailedException e) {
                throw new IllegalStateException("Could not run example because of an exception", e);
            }
        };
        MeasurementChain.of("Example Execution", example)
                .measure(ExecutionTimeMeasurement.getInstance())
                .measure(HeapMemoryMeasurement.getInstance())
                .run();
    }

    private void printResult(final DataFinderResult<WorldCityMetadata, WorldCity> result) {
        System.out.println(""
                + "City: " + result.getResource().getData().getName() + "; "
                + "Country: " + result.getResource().getData().getCountry() + "; "
                + "Score: " + result.getScore());
    }
}
