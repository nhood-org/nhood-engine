package com.h8.nh.nhoodengine.example.songs.model;

import java.util.Objects;

public final class Song {

    private final String id;
    private final String artist;
    private final String title;

    private Song(final String id, final String artist, final String title) {
        this.id = id;
        this.artist = artist;
        this.title = title;
    }

    public static Song of(final String id, final String artist, final String title) {
        return new Song(id, artist, title);
    }

    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Song song = (Song) o;
        return id.equals(song.id)
                && artist.equals(song.artist)
                && title.equals(song.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, artist, title);
    }
}
