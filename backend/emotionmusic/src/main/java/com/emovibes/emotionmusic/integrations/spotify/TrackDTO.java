package com.emovibes.emotionmusic.integrations.spotify;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDTO {
    private String id;
    private String name;
    private String previewUrl;
    private String externalUrl;
    private Artist[] artists;
    private Album album;

    public TrackDTO() {}

    public TrackDTO(String id, String name, String previewUrl, String externalUrl, Artist[] artists, Album album) {
        this.id = id;
        this.name = name;
        this.previewUrl = previewUrl;
        this.externalUrl = externalUrl;
        this.artists = artists;
        this.album = album;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @JsonProperty("preview_url")
    public String getPreviewUrl() { return previewUrl; }
    public void setPreviewUrl(String previewUrl) { this.previewUrl = previewUrl; }

    @JsonProperty("external_urls")
    public ExternalUrls getExternalUrls() { 
        return new ExternalUrls(externalUrl); 
    }
    public void setExternalUrls(ExternalUrls externalUrls) { 
        this.externalUrl = externalUrls.getSpotify(); 
    }

    public Artist[] getArtists() { return artists; }
    public void setArtists(Artist[] artists) { this.artists = artists; }
    
    // Helper method to get first artist name
    public String getFirstArtistName() {
        return artists != null && artists.length > 0 ? artists[0].getName() : "Unknown Artist";
    }

    public Album getAlbum() { return album; }
    public void setAlbum(Album album) { this.album = album; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExternalUrls {
        private String spotify;
        
        public ExternalUrls() {}
        public ExternalUrls(String spotify) { this.spotify = spotify; }
        
        public String getSpotify() { return spotify; }
        public void setSpotify(String spotify) { this.spotify = spotify; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Artist {
        private String name;
        
        public Artist() {}
        public Artist(String name) { this.name = name; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Album {
        private String name;
        private String imageUrl;
        
        public Album() {}
        public Album(String name, String imageUrl) { 
            this.name = name; 
            this.imageUrl = imageUrl; 
        }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        @JsonProperty("images")
        public Image[] getImages() { 
            return new Image[]{new Image(imageUrl)}; 
        }
        public void setImages(Image[] images) { 
            this.imageUrl = images.length > 0 ? images[0].getUrl() : null; 
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private String url;
        
        public Image() {}
        public Image(String url) { this.url = url; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
