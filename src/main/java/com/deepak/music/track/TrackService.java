package com.deepak.music.track;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import com.deepak.music.common.exception.ArtistNotFoundException;
import com.deepak.music.track.dto.CreateTrackRequest;
import com.deepak.music.track.dto.TrackResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TrackService {
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;

    public TrackService(TrackRepository trackRepository, ArtistRepository artistRepository) {
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
    }

    @Transactional
    public Track addTrack(UUID artistId, CreateTrackRequest request) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));

        Track track = new Track();
        track.setTitle(request.title());
        track.setGenre(request.genre());
        track.setDurationSeconds(request.durationSeconds());
        track.setArtist(artist);

        return trackRepository.save(track);
    }

    public Page<Track> listTracks(UUID artistId, Genre genre, Pageable pageable) {
        // Verify artist exists first - return 404 if not
        if(!artistRepository.existsById(artistId)) {
            throw new ArtistNotFoundException(artistId);
        }

        if(genre != null) {
            return trackRepository.findAllByArtistIdAndGenre(artistId, genre, pageable);
        }

        return trackRepository.findAllByArtistId(artistId, pageable);
    }
}
