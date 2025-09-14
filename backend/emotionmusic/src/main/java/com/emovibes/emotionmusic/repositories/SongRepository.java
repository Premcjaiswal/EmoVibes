package com.emovibes.emotionmusic.repositories;

import com.emovibes.emotionmusic.models.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
    // Custom queries can be added here later
}
