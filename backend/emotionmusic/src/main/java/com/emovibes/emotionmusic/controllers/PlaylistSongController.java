package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.models.PlaylistSong;
import com.emovibes.emotionmusic.repositories.PlaylistSongRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist-songs")
public class PlaylistSongController {

    private final PlaylistSongRepository repository;

    public PlaylistSongController(PlaylistSongRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{playlistId}")
    public List<PlaylistSong> getSongsByPlaylistId(@PathVariable Long playlistId) {
        return repository.findByPlaylistId(playlistId);
    }

    @PostMapping
    public PlaylistSong addSongToPlaylist(@RequestBody PlaylistSong playlistSong) {
        return repository.save(playlistSong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable Long id) {
        return repository.findById(id)
                .map(playlistSong -> {
                    repository.delete(playlistSong);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
