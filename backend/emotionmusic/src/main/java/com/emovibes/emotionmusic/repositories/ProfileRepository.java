package com.emovibes.emotionmusic.repositories;

import com.emovibes.emotionmusic.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(String userId);
}
