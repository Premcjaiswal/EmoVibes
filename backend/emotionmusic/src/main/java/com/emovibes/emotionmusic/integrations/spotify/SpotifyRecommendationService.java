package com.emovibes.emotionmusic.integrations.spotify;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SpotifyRecommendationService {

    // text
    private final SpotifyAuthClient authClient;
    private final WebClient webClient;
    private final String apiBase;
    private final ObjectMapper objectMapper;

    public SpotifyRecommendationService(
            SpotifyAuthClient authClient,
            WebClient.Builder builder,
            @Value("${spotify.api-base}") String apiBase,
            ObjectMapper objectMapper) {
        this.authClient = authClient;
        this.apiBase = apiBase;
        this.objectMapper = objectMapper;
        // Configure a base URL so we can use relative paths safely
        this.webClient = builder.baseUrl(apiBase).build();
    }

    public Mono<String> recommendByEmotion(String emotion) {
        EmotionParams params = EmotionParams.map(emotion);
        // Build a search query using the first genre seed
        String firstGenre = params.seedGenres.contains(",")
                ? params.seedGenres.split(",")[0]
                : params.seedGenres;
        
        System.out.println("Emotion: " + emotion + " -> Genre: " + firstGenre);

        return authClient.getAccessToken()
                .flatMap(token ->
                        webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/search")
                                        .queryParam("q", "genre:\"" + firstGenre + "\"")
                                        .queryParam("type", "track")
                                        .queryParam("limit", 20)
                                        .build())
                                .headers(h -> h.setBearerAuth(token))
                                .retrieve()
                                .onStatus(status -> status.isError(), clientResponse ->
                                        clientResponse.bodyToMono(String.class)
                                                .defaultIfEmpty("")
                                                .flatMap(body -> Mono.error(new RuntimeException(
                                                        "Spotify API error (" + clientResponse.statusCode().value() + "): " + body))))
                                .bodyToMono(SpotifySearchResponse.class)
                                .map(response -> formatTracksResponse(response, emotion))
                );
    }

    private String formatTracksResponse(SpotifySearchResponse response, String emotion) {
        try {
            StringBuilder formatted = new StringBuilder();
            formatted.append("{\n");
            formatted.append("  \"emotion\": \"").append(emotion).append("\",\n");
            formatted.append("  \"total_tracks\": ").append(response.getTracks().getTotal()).append(",\n");
            formatted.append("  \"tracks\": [\n");
            
            TrackDTO[] tracks = response.getTracks().getItems();
            for (int i = 0; i < tracks.length; i++) {
                TrackDTO track = tracks[i];
                formatted.append("    {\n");
                formatted.append("      \"id\": \"").append(track.getId()).append("\",\n");
                formatted.append("      \"name\": \"").append(escapeJson(track.getName())).append("\",\n");
                formatted.append("      \"artist\": \"").append(escapeJson(track.getFirstArtistName())).append("\",\n");
                formatted.append("      \"album\": \"").append(escapeJson(track.getAlbum().getName())).append("\",\n");
                formatted.append("      \"preview_url\": \"").append(track.getPreviewUrl() != null ? track.getPreviewUrl() : "").append("\",\n");
                formatted.append("      \"spotify_url\": \"").append(track.getExternalUrls().getSpotify()).append("\"\n");
                formatted.append("    }");
                if (i < tracks.length - 1) {
                    formatted.append(",");
                }
                formatted.append("\n");
            }
            
            formatted.append("  ]\n");
            formatted.append("}");
            return formatted.toString();
        } catch (Exception e) {
            return "{\"error\": \"Failed to format response: " + e.getMessage() + "\"}";
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    public Mono<String> availableGenres() {
        // Fallback: static list aligned with our emotion mapping
        String staticGenresJson = "{\"genres\":[\"pop\",\"acoustic\",\"metal\",\"chill\",\"edm\"]}";
        return Mono.just(staticGenresJson);
    }

    static class EmotionParams {
        final String seedGenres;
        final Double targetValence;
        final Double targetEnergy;

        EmotionParams(String seedGenres, Double v, Double e) {
            this.seedGenres = seedGenres;
            this.targetValence = v;
            this.targetEnergy = e;
        }

        static EmotionParams map(String emotion) {
            String e = emotion == null ? "" : emotion.trim().toLowerCase();
            return switch (e) {
                case "happy", "joy" -> new EmotionParams("pop", 0.9, 0.8);
                case "sad" -> new EmotionParams("acoustic", 0.2, 0.3);
                case "angry" -> new EmotionParams("metal", 0.3, 0.9);
                case "calm", "relaxed" -> new EmotionParams("chill", 0.6, 0.2);
                case "energetic", "excited" -> new EmotionParams("edm", 0.8, 0.95);
                default -> new EmotionParams("pop", 0.5, 0.5);
            };
        }
    }
}


