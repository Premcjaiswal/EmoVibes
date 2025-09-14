package com.emovibes.emotionmusic.repositories;

import com.emovibes.emotionmusic.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    List<Playlist> findByUserId(String userId);
}
