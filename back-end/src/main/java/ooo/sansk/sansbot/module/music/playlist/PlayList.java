package ooo.sansk.sansbot.module.music.playlist;

import ooo.sansk.sansbot.repository.Identifyable;

import java.util.List;

public class PlayList implements Identifyable<String> {

    private final String id;
    private final List<Track> trackList;

    public PlayList(String id, List<Track> trackList) {
        this.id = id;
        this.trackList = trackList;
    }

    public String getId() {
        return id;
    }

    public List<Track> getTrackList() {
        return trackList;
    }
}
