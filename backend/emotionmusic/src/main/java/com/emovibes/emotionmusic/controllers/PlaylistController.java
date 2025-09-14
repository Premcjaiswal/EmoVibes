package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.models.Playlist;
import com.emovibes.emotionmusic.repositories.PlaylistRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistRepository repository;

    public PlaylistController(PlaylistRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Playlist> getAllPlaylists() {
        return repository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<Playlist> getPlaylistsByUserId(@PathVariable String userId) {
        return repository.findByUserId(userId);
    }

    @PostMapping
    public Playlist createPlaylist(@RequestBody Playlist playlist) {
        return repository.save(playlist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Playlist> updatePlaylist(@PathVariable Long id, @RequestBody Playlist updatedPlaylist) {
        return repository.findById(id)
                .map(playlist -> {
                    playlist.setTitle(updatedPlaylist.getTitle());
                    playlist.setDescription(updatedPlaylist.getDescription());
                    return ResponseEntity.ok(repository.save(playlist));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Long id) {
        return repository.findById(id)
                .map(playlist -> {
                    repository.delete(playlist);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
