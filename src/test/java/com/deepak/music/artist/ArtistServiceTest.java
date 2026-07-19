package com.deepak.music.artist;

import java.util.Optional;
import java.util.UUID;
import com.deepak.music.artist.dto.CreateArtistRequest;
import com.deepak.music.common.exception.ArtistNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}