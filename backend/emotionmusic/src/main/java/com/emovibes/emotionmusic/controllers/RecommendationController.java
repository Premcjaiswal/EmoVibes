package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.integrations.spotify.SpotifyRecommendationService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    // text
    private final SpotifyRecommendationService svc;

    public RecommendationController(SpotifyRecommendationService svc) {
        this.svc = svc;
    }

    @GetMapping("/genres")
    public Mono<String> genres() {
        return svc.availableGenres();
    }

    @GetMapping("/{emotion}")
    public Mono<String> recommend(@PathVariable String emotion) {
        return svc.recommendByEmotion(emotion);
    }
}


