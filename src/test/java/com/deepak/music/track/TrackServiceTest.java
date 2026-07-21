package com.deepak.music.track;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import com.deepak.music.common.exception.ArtistNotFoundException;
import com.deepak.music.track.dto.CreateTrackRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrackServiceTest {

    @Mock
    private TrackRepository trackRepository;

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private TrackService trackService;

    @Test
    void testAddTrackSuccess() {
        UUID artistId = UUID.randomUUID();
        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setName("Daft Punk");

        CreateTrackRequest request = new CreateTrackRequest("Harder Better Faster Stronger", Genre.ELECTRONIC, 224);

        Track savedTrack = new Track();
        savedTrack.setId(UUID.randomUUID());
        savedTrack.setArtist(artist);
        savedTrack.setTitle(request.title());
        savedTrack.setGenre(request.genre());
        savedTrack.setDurationSeconds(request.durationSeconds());

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(trackRepository.save(any(Track.class))).thenReturn(savedTrack);

        Track result = trackService.addTrack(artistId, request);

        assertNotNull(result);
        assertEquals(artistId, result.getArtist().getId());
        assertEquals("Harder Better Faster Stronger", result.getTitle());
        assertEquals(Genre.ELECTRONIC, result.getGenre());
        assertEquals(224, result.getDurationSeconds());
        verify(artistRepository, times(1)).findById(artistId);
        verify(trackRepository, times(1)).save(any(Track.class));
    }

    @Test
    void testAddTrackArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        CreateTrackRequest request = new CreateTrackRequest("Numb", Genre.ROCK, 187);

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> trackService.addTrack(artistId, request));
        verify(artistRepository, times(1)).findById(artistId);
        verify(trackRepository, never()).save(any(Track.class));
    }

    @Test
    void testListTracksSuccess() {
        UUID artistId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        Artist artist = new Artist();
        artist.setId(artistId);

        Track track = new Track();
        track.setId(UUID.randomUUID());
        track.setArtist(artist);
        track.setTitle("Come As You Are");
        track.setGenre(Genre.ROCK);
        track.setDurationSeconds(219);

        Page<Track> expectedPage = new PageImpl<>(List.of(track));

        when(artistRepository.existsById(artistId)).thenReturn(true);
        when(trackRepository.findAllByArtistId(artistId, pageable)).thenReturn(expectedPage);

        Page<Track> result = trackService.listTracks(artistId, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(trackRepository, times(1)).findAllByArtistId(artistId, pageable);
        verify(trackRepository, never()).findAllByArtistIdAndGenre(any(), any(), any());
    }

    @Test
    void testListTracksWithGenreFilter() {
        UUID artistId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(artistRepository.existsById(artistId)).thenReturn(true);
        when(trackRepository.findAllByArtistIdAndGenre(artistId, Genre.ROCK, pageable))
                .thenReturn(Page.empty());

        Page<Track> result = trackService.listTracks(artistId, Genre.ROCK, pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(trackRepository, times(1)).findAllByArtistIdAndGenre(artistId, Genre.ROCK, pageable);
        verify(trackRepository, never()).findAllByArtistId(any(), any());
    }

    @Test
    void testListTracksArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        when(artistRepository.existsById(artistId)).thenReturn(false);

        assertThrows(ArtistNotFoundException.class,
                () -> trackService.listTracks(artistId, null, pageable));
        verify(trackRepository, never()).findAllByArtistId(any(), any());
    }
}

