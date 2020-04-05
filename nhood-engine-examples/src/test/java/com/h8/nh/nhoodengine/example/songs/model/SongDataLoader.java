package com.h8.nh.nhoodengine.example.songs.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.Scanner;

public final class SongDataLoader {
    private static final String SONGS_METADATA_RESOURCE_FILE = "songs/vectors.out.csv";
    private static final String SONGS_METADATA_RESOURCE_FILE_DELIMITER = ":\\|:";

    private final DataMatrixRepository<SongMetadata, Song> repository;

    public SongDataLoader(
            final DataMatrixRepository<SongMetadata, Song> repository) {
        this.repository = repository;
    }

    public void load()
            throws DataMatrixRepositoryFailedException {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(SONGS_METADATA_RESOURCE_FILE);

        if (in == null) {
            throw new IllegalStateException("Could not load resource file");
        }

        try (Scanner scanner = new Scanner(in)) {
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Resource file is empty");
            }

            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(SONGS_METADATA_RESOURCE_FILE_DELIMITER);

                SongMetadata songMetadata = SongMetadata.of(
                        new double[]{
                                mapDoubleValue(row[3]),
                                mapDoubleValue(row[4]),
                                mapDoubleValue(row[5]),
                                mapDoubleValue(row[6]),
                                mapDoubleValue(row[7]),
                                mapDoubleValue(row[8]),
                                mapDoubleValue(row[9]),
                                mapDoubleValue(row[10]),
                                mapDoubleValue(row[11]),
                                mapDoubleValue(row[12]),
                        });

                if (songMetadata.isZeroVector()) {
                    continue;
                }

                Song song = Song.of(
                        row[0],
                        row[1],
                        row[2]
                );

                DataResource<SongMetadata, Song> resource = DataResource.<SongMetadata, Song>builder()
                        .key(songMetadata)
                        .data(song)
                        .build();

                repository.add(resource);
            }
        }
    }

    private double mapDoubleValue(final String value) {
        String s = mapStringValue(value);
        if ("".equals(s)) {
            return Double.MIN_VALUE;
        } else {
            return Double.parseDouble(s);
        }
    }

    private String mapStringValue(final String value) {
        return value.replace("\"", "");
    }

    private static class SongComposite {
        private Song song;
        private SongMetadata songMetadata;
    }
}
