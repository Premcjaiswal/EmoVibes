package com.emovibes.emotionmusic.integrations.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifySearchResponse {
    private Tracks tracks;

    public Tracks getTracks() { return tracks; }
    public void setTracks(Tracks tracks) { this.tracks = tracks; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tracks {
        private TrackDTO[] items;
        private int total;

        public TrackDTO[] getItems() { return items; }
        public void setItems(TrackDTO[] items) { this.items = items; }

        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
    }
}
