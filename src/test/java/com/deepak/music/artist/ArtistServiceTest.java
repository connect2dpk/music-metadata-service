package com.deepak.music.artist;

import java.util.Optional;
import java.util.UUID;
import com.deepak.music.artist.dto.CreateArtistRequest;
import com.deepak.music.artist.dto.UpdateArtistNameRequest;
import com.deepak.music.common.exception.ArtistNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    @Test
    void testCreateArtist() {
        CreateArtistRequest request = new CreateArtistRequest("The Beatles");
        Artist artist = new Artist();
        artist.setId(UUID.randomUUID());
        artist.setName("The Beatles");

        when(artistRepository.save(any(Artist.class))).thenReturn(artist);

        Artist result = artistService.create(request);

        assertNotNull(result);
        assertEquals("The Beatles", result.getName());
        verify(artistRepository, times(1)).save(any(Artist.class));
    }

    @Test
    void testGetArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistService.getById(artistId));
    }

    @Test
    void testGetArtistSuccess() {
        UUID artistId = UUID.randomUUID();
        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setName("The Beatles");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        Artist result = artistService.getById(artistId);

        assertNotNull(result);
        assertEquals(artistId, result.getId());
        assertEquals("The Beatles", result.getName());
    }

    @Test
    void testListArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> expectedPage = new PageImpl<>(java.util.List.of(new Artist()));
        when(artistRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Artist> result = artistService.list(pageable);

        assertSame(expectedPage, result);
        verify(artistRepository, times(1)).findAll(pageable);
    }

    @Test
    void testUpdateNameSuccess() {
        UUID artistId = UUID.randomUUID();
        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setName("Old Name");

        UpdateArtistNameRequest request = new UpdateArtistNameRequest("New Name");

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.save(artist)).thenReturn(artist);

        Artist result = artistService.updateName(artistId, request);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        verify(artistRepository, times(1)).findById(artistId);
        verify(artistRepository, times(1)).save(artist);
    }

    @Test
    void testUpdateNameArtistNotFound() {
        UUID artistId = UUID.randomUUID();
        UpdateArtistNameRequest request = new UpdateArtistNameRequest("New Name");

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> artistService.updateName(artistId, request));
        verify(artistRepository, times(1)).findById(artistId);
        verify(artistRepository, never()).save(any(Artist.class));
    }
}