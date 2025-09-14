package com.emovibes.emotionmusic.integrations.spotify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SpotifyAuthClient {

    // text
    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    public SpotifyAuthClient(
            WebClient.Builder builder,
            @Value("${spotify.client-id}") String clientId,
            @Value("${spotify.client-secret}") String clientSecret,
            @Value("${spotify.token-url}") String tokenUrl) {
        this.webClient = builder.build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = tokenUrl;
    }

    public Mono<String> getAccessToken() {
        String basic = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");

        return webClient.post()
                .uri(tokenUrl)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basic)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(form)
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccessToken);
    }

    public static class TokenResponse {
        private String access_token;
        private String token_type;
        private long expires_in;
        public String getAccessToken() { return access_token; }
        public void setAccess_token(String access_token) { this.access_token = access_token; }
        public String getToken_type() { return token_type; }
        public void setToken_type(String token_type) { this.token_type = token_type; }
        public long getExpires_in() { return expires_in; }
        public void setExpires_in(long expires_in) { this.expires_in = expires_in; }
    }
}


