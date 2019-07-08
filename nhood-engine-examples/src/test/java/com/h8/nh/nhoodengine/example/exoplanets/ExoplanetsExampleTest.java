package com.h8.nh.nhoodengine.example.exoplanets;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.impl.DataScoreComputationEngine;
import com.h8.nh.nhoodengine.example.exoplanets.model.Exoplanet;
import com.h8.nh.nhoodengine.example.exoplanets.model.ExoplanetDataLoader;
import com.h8.nh.nhoodengine.example.exoplanets.model.ExoplanetMetadata;
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
 * There is a set of all known exoplanets loaded to the repository.
 * (Database by PHL's Exoplanet Catalog of the Planetary Habitability Laboratory @ UPR Arecibo)
 * <p>
 * In order to eliminate an influence of value scale differences a feature scaling has been applied.
 * <p>
 * DataFinder is used to resolve a planet
 * with the characteristics similar to the earth characteristics.
 * The example should return Kepler-452b which is quoted an Earth 2.0 or Earth's Cousin.
 * (See more: https://www.space.com/30034-earth-cousin-exoplanet-kepler-452b-life.html)
 */
class ExoplanetsExampleTest {

    @Test
    void runExample()
            throws DataFinderFailedException, DataMatrixRepositoryFailedException {
        DataMatrixRepository<ExoplanetMetadata, Exoplanet> repository =
                new DataMatrixCellBasedRepository<>(ExoplanetMetadata.METADATA_SIZE);
        new ExoplanetDataLoader(repository).load();

        DataFinder<ExoplanetMetadata, Exoplanet> finder =
                new DataScoreComputationEngine<>(repository);

        ExoplanetMetadata EARTH_METADATA = ExoplanetMetadata.builder()
                .mass(1.0d)
                .radius(1.0d)
                .period(365.0d)
                .temperature(255.0d)
                .periastron(0.983d)
                .apastron(1.016d)
                .flux(1.0d)
                .starMass(1.0d)
                .starRadius(1.0d)
                .starDistance(1.0d)
                .starLuminosity(1.0d)
                .build();

        DataFinderCriteria<ExoplanetMetadata> criteria =
                DataFinderCriteria.<ExoplanetMetadata>builder()
                        .limit(1)
                        .metadata(EARTH_METADATA)
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

    private void printResult(final DataFinderResult<ExoplanetMetadata, Exoplanet> result) {
        System.out.println(""
                + "Planet: " + result.getResource().getData().getName() + "; "
                + "Score: " + result.getScore());
    }
}
