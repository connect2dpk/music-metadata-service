package com.deepak.music.artistoftheday;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ArtistOfTheDayService {
    private static final Logger log = LoggerFactory.getLogger(ArtistOfTheDayService.class);

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
            log.info("Artist of the day requested but no artists exist in the catalogue");
            return Optional.empty();
        }

        long epochDay = LocalDate.now(clock).toEpochDay();
        long offset = Math.floorMod(epochDay, totalArtists);

        log.debug("Selecting artist of the day using epochDay={} totalArtists={} offset={}", epochDay, totalArtists, offset);

        Optional<Artist> artist = artistRepository.findArtistAtOffset(offset);
        artist.ifPresent(value -> log.info("Artist of the day selected id={} name='{}'", value.getId(), value.getName()));
        return artist;
    }
}
