package com.emovibes.emotionmusic.controllers;

import com.emovibes.emotionmusic.models.Profile;
import com.emovibes.emotionmusic.repositories.ProfileRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileRepository repository;

    public ProfileController(ProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Profile> getAllProfiles() {
        return repository.findAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Profile> getProfileByUserId(@PathVariable String userId) {
        return repository.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Profile createProfile(@RequestBody Profile profile) {
        return repository.save(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile updatedProfile) {
        return repository.findById(id)
                .map(profile -> {
                    profile.setName(updatedProfile.getName());
                    profile.setEmail(updatedProfile.getEmail());
                    profile.setPreferences(updatedProfile.getPreferences());
                    return ResponseEntity.ok(repository.save(profile));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        return repository.findById(id)
                .map(profile -> {
                    repository.delete(profile);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
