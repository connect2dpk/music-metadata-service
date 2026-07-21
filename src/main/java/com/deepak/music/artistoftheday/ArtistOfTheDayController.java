package com.deepak.music.artistoftheday;

import com.deepak.music.artist.dto.ArtistResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtistOfTheDayController {
    private final ArtistOfTheDayService artistOfTheDayService;

    public ArtistOfTheDayController(ArtistOfTheDayService artistOfTheDayService) {
        this.artistOfTheDayService = artistOfTheDayService;
    }

    @GetMapping("/api/v1/artist-of-the-day")
    public ResponseEntity<ArtistResponse> getArtistOfTheDay() {
        return artistOfTheDayService.getArtistOfTheDay()
                .map(ArtistResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

}
