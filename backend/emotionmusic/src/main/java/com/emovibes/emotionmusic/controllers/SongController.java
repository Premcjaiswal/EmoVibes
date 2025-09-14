package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.models.Song;
import com.emovibes.emotionmusic.repositories.SongRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    private final SongRepository repository;

    public SongController(SongRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Song> getAllSongs() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Song createSong(@RequestBody Song song) {
        return repository.save(song);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Song> updateSong(@PathVariable Long id, @RequestBody Song updatedSong) {
        return repository.findById(id)
                .map(song -> {
                    song.setTitle(updatedSong.getTitle());
                    song.setArtist(updatedSong.getArtist());
                    song.setAlbum(updatedSong.getAlbum());
                    song.setFeatures(updatedSong.getFeatures());
                    return ResponseEntity.ok(repository.save(song));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable Long id) {
        return repository.findById(id)
                .map(song -> {
                    repository.delete(song);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
