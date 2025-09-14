package com.emovibes.emotionmusic.repositories;

import com.emovibes.emotionmusic.models.Emotion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    List<Emotion> findByUserId(String userId);
}
