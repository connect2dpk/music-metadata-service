package com.deepak.music.artistoftheday;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ArtistOfTheDayService {
    private final ArtistRepository artistRepository;
    private final Clock clock;

    @Autowired
    public ArtistOfTheDayService(ArtistRepository artistRepository) {
        this(artistRepository, Clock.systemUTC());
    }

    // Package private for unit tests to inject fixed clock
    ArtistOfTheDayService(ArtistRepository artistRepository, Clock clock) {
        this.artistRepository = artistRepository;
        this.clock = clock;
    }

    public Optional<Artist> getArtistOfTheDay() {
        long totalArtists = artistRepository.count();
        if(totalArtists == 0) {
            return Optional.empty();
        }

        long epochDay = LocalDate.now(clock).toEpochDay();
        long offset = Math.floorMod(epochDay, totalArtists);

        return artistRepository.findArtistAtOffset(offset);
    }
}
