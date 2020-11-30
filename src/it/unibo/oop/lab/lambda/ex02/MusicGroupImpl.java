package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;


/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream().map(x-> x.getSongName()).sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet().stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream().filter(x->x.getValue()==year).map(x->x.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) songs.stream().filter(x -> x.getAlbumName().orElse("").equals(albumName)).count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) songs.stream().filter(x -> x.getAlbumName().isEmpty()).count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return songs.stream().filter(x->x.getAlbumName().equals(Optional.ofNullable(albumName))).mapToDouble(x->x.duration).average();
    }

    @Override
    public Optional<String> longestSong() {
    	return Optional.ofNullable(this.songs.stream().max( (x,y) -> {
    		return (int) ( x.getDuration() - y.getDuration());
    	}).get().getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        Map<String, Double> mapAlbumDuration = new HashMap<>();
        albums.keySet().stream().forEach(x -> {
        	Double duration =this.songs.stream().filter( y -> y.getAlbumName().equals(Optional.ofNullable(x))).mapToDouble(z -> z.getDuration()).sum();
        	mapAlbumDuration.put(x, duration);
        });
        double max = 0;
        String maxStr = null;
        for(var entry: mapAlbumDuration.entrySet()) {
        	if(entry.getValue() > max) {
        		max = entry.getValue();
        		maxStr = entry.getKey();
        	}
        }
        return Optional.ofNullable(maxStr);
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
