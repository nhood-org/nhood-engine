package com.h8.nh.nhoodengine.example.songs.model;

import com.h8.nh.nhoodengine.core.DataResource;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepository;
import com.h8.nh.nhoodengine.matrix.DataMatrixRepositoryFailedException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public final class SongDataLoader {
    private static final String SONGS_RESOURCE_FILE = "songs/tracks.csv";
    private static final String SONGS_RESOURCE_FILE_DELIMITER = ", ";
    private static final String SONGS_METADATA_RESOURCE_FILE = "songs/vectors.csv";
    private static final String SONGS_METADATA_RESOURCE_FILE_DELIMITER = " ";

    private final Map<String, SongComposite> songs;
    private final DataMatrixRepository<SongMetadata, Song> repository;

    public SongDataLoader(
            final DataMatrixRepository<SongMetadata, Song> repository) {
        this.songs = new HashMap<>();
        this.repository = repository;
    }

    public void load()
            throws DataMatrixRepositoryFailedException {
        loadSongs();
        loadSongsMetadata();

        for (SongComposite c : songs.values()) {
            if (c.songMetadata == null) {
                continue;
            }

            DataResource<SongMetadata, Song> resource = DataResource.<SongMetadata, Song>builder()
                    .key(c.songMetadata)
                    .data(c.song)
                    .build();

            repository.add(resource);
        }
    }

    private void loadSongs() {
        InputStream in = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(SONGS_RESOURCE_FILE);

        if (in == null) {
            throw new IllegalStateException("Could not load resource file");
        }

        try (Scanner scanner = new Scanner(in)) {
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Resource file is empty");
            }

            while (scanner.hasNextLine()) {
                String[] row = scanner.nextLine().split(SONGS_RESOURCE_FILE_DELIMITER);

                SongComposite c = new SongComposite();
                c.song = Song.of(row[0], row[1], row[2]);
                c.songMetadata = null;

                songs.put(c.song.getId(), c);
            }
        }
    }

    private void loadSongsMetadata() {
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

                String id = row[0];
                SongMetadata m = SongMetadata.of(
                        new double[]{
                                mapDoubleValue(row[1]),
                                mapDoubleValue(row[2]),
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
                                mapDoubleValue(row[13]),
                                mapDoubleValue(row[14]),
                                mapDoubleValue(row[15]),
                        });

                if (songs.containsKey(id)) {
                    SongComposite c = songs.get(id);
                    c.songMetadata = m;
                    songs.put(id, c);
                }
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
