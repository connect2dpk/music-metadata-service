package com.deepak.music.track;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import com.deepak.music.common.exception.ArtistNotFoundException;
import com.deepak.music.track.dto.CreateTrackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TrackService {
    private static final Logger log = LoggerFactory.getLogger(TrackService.class);

    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    public TrackService(TrackRepository trackRepository, ArtistRepository artistRepository) {
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional
    public Track addTrack(UUID artistId, CreateTrackRequest request) {
        log.info("Adding track for artistId={} title='{}' genre={} durationSeconds={}",
                artistId, request.title(), request.genre(), request.durationSeconds());
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));

        Track track = new Track();
        track.setTitle(request.title());
        track.setGenre(request.genre());
        track.setDurationSeconds(request.durationSeconds());
        track.setArtist(artist);

        Track savedTrack = trackRepository.save(track);
        log.info("Added track id={} for artistId={} title='{}'", savedTrack.getId(), artistId, savedTrack.getTitle());
        return savedTrack;
    }

    public Page<Track> listTracks(UUID artistId, Genre genre, Pageable pageable) {
        log.debug("Listing tracks for artistId={} genre={} page={} size={} sort={}",
                artistId, genre, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        if(!artistRepository.existsById(artistId)) {
            throw new ArtistNotFoundException(artistId);
        }

        if(genre != null) {
            return trackRepository.findAllByArtistIdAndGenre(artistId, genre, pageable);
        }

        return trackRepository.findAllByArtistId(artistId, pageable);
    }
}
