package com.h8.nh.nhoodengine.example.songs;

import com.h8.nh.nhoodengine.core.DataFinder;
import com.h8.nh.nhoodengine.core.DataFinderCriteria;
import com.h8.nh.nhoodengine.core.DataFinderFailedException;
import com.h8.nh.nhoodengine.core.DataFinderResult;
import com.h8.nh.nhoodengine.core.impl.DataScoreComputationEngine;
import com.h8.nh.nhoodengine.example.songs.model.Song;
import com.h8.nh.nhoodengine.example.songs.model.SongDataLoader;
import com.h8.nh.nhoodengine.example.songs.model.SongMetadata;
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
 * There is a set of songs with its metadata vectors loaded to the repository.
 * Songs database is a last.fm database downloaded from http://millionsongdataset.com/lastfm/
 * All of the songs metadata vectors were resolved with word2vec algorithm.
 * <p>
 * DataFinder is used to resolve 10 of songs
 * closest to the song: The Prodigy - Breathe.
 * (R.I.P Keith Flint)
 */
class SongsExampleTest {

    @Test
    void runExample()
            throws DataFinderFailedException, DataMatrixRepositoryFailedException {
        DataMatrixRepository<SongMetadata, Song> repository =
                new DataMatrixCellBasedRepository<>(SongMetadata.METADATA_SIZE);
        new SongDataLoader(repository).load();

        DataFinder<SongMetadata, Song> finder =
                new DataScoreComputationEngine<>(repository);

        int SONGS_LIMIT = 10;
        double[] THE_PRODIGY_BREATHE_VECTOR = new double[]{
                193.2216594648,
                270.2663400872,
                365.4783524283,
                613.3806819591,
                369.0870999666,
                223.8209770800,
                358.9450120825,
                125.9842852265,
                182.3609437575,
                32.0263064546
        };

        DataFinderCriteria<SongMetadata> criteria =
                DataFinderCriteria.<SongMetadata>builder()
                        .limit(SONGS_LIMIT)
                        .metadata(SongMetadata.of(THE_PRODIGY_BREATHE_VECTOR))
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

    private void printResult(final DataFinderResult<SongMetadata, Song> result) {
        System.out.println(""
                + "Artist: " + result.getResource().getData().getArtist() + "; "
                + "Title: " + result.getResource().getData().getTitle() + "; "
                + "Score: " + result.getScore());
    }
}
