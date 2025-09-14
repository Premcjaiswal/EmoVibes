package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.models.Emotion;
import com.emovibes.emotionmusic.repositories.EmotionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emotions")
public class EmotionController {

    private final EmotionRepository repository;

    public EmotionController(EmotionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Emotion> getAllEmotions() {
        return repository.findAll();
    }

    @GetMapping("/{userId}")
    public List<Emotion> getEmotionsByUserId(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping
    public Emotion createEmotion(@RequestBody Emotion emotion) {
        return repository.save(emotion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emotion> updateEmotion(@PathVariable Long id, @RequestBody Emotion updatedEmotion) {
        return repository.findById(id)
                .map(emotion -> {
                    emotion.setEmotionType(updatedEmotion.getEmotionType());
                    emotion.setConfidenceScore(updatedEmotion.getConfidenceScore());
                    emotion.setInputType(updatedEmotion.getInputType());
                    emotion.setMeta(updatedEmotion.getMeta());
                    return ResponseEntity.ok(repository.save(emotion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmotion(@PathVariable Long id) {
        return repository.findById(id)
                .map(emotion -> {
                    repository.delete(emotion);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
