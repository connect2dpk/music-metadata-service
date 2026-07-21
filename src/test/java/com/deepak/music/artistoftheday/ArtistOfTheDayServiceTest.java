package com.deepak.music.artistoftheday;

import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistOfTheDayServiceTest {

    private final ArtistRepository artistRepository = mock(ArtistRepository.class);

    @Test
    void testNoArtistsReturnsEmpty() {
        ArtistOfTheDayService service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("1970-01-02T00:00:00Z"), ZoneOffset.UTC)
        );

        when(artistRepository.count()).thenReturn(0L);

        Optional<Artist> result = service.getArtistOfTheDay();

        assertTrue(result.isEmpty());
        verify(artistRepository, times(1)).count();
        verify(artistRepository, never()).findArtistAtOffset(anyLong());
    }

    @Test
    void testSingleArtistAlwaysSelected() {
        Artist artist = new Artist();
        artist.setId(UUID.randomUUID());
        artist.setName("Solo Artist");

        ArtistOfTheDayService service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("2026-07-20T00:00:00Z"), ZoneOffset.UTC)
        );

        when(artistRepository.count()).thenReturn(1L);
        when(artistRepository.findArtistAtOffset(0L)).thenReturn(Optional.of(artist));

        Optional<Artist> result = service.getArtistOfTheDay();

        assertTrue(result.isPresent());
        assertEquals(artist.getId(), result.get().getId());
        verify(artistRepository, times(1)).findArtistAtOffset(0L);
    }

    @Test
    void testOffsetCalculationForNArtists() {
        // 1970-01-06 => epochDay = 5, 5 mod 3 = 2
        Artist expected = new Artist();
        expected.setId(UUID.randomUUID());
        expected.setName("Artist C");

        ArtistOfTheDayService service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("1970-01-06T00:00:00Z"), ZoneOffset.UTC)
        );

        when(artistRepository.count()).thenReturn(3L);
        when(artistRepository.findArtistAtOffset(2L)).thenReturn(Optional.of(expected));

        Optional<Artist> result = service.getArtistOfTheDay();

        assertTrue(result.isPresent());
        assertEquals(expected.getId(), result.get().getId());
        verify(artistRepository, times(1)).findArtistAtOffset(2L);
    }

    @Test
    void testCycleRepeatsAfterNdays() {
        Artist expected = new Artist();
        expected.setId(UUID.randomUUID());
        expected.setName("Repeat Artist");

        ArtistOfTheDayService day1Service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("1970-01-05T00:00:00Z"), ZoneOffset.UTC) // epochDay 4
        );
        ArtistOfTheDayService day2Service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("1970-01-08T00:00:00Z"), ZoneOffset.UTC) // epochDay 7, +3 days
        );

        when(artistRepository.count()).thenReturn(3L);
        when(artistRepository.findArtistAtOffset(1L)).thenReturn(Optional.of(expected));

        Optional<Artist> first = day1Service.getArtistOfTheDay();
        Optional<Artist> second = day2Service.getArtistOfTheDay();

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertEquals(first.get().getId(), second.get().getId());
        verify(artistRepository, times(2)).findArtistAtOffset(1L);
    }

    @Test
    void testSameDayReturnsSameArtist() {
        Artist expected = new Artist();
        expected.setId(UUID.randomUUID());
        expected.setName("Stable Artist");

        ArtistOfTheDayService service = new ArtistOfTheDayService(
                artistRepository,
                Clock.fixed(Instant.parse("2026-07-20T12:30:00Z"), ZoneOffset.UTC)
        );

        when(artistRepository.count()).thenReturn(4L);
        when(artistRepository.findArtistAtOffset(2L)).thenReturn(Optional.of(expected));

        Optional<Artist> first = service.getArtistOfTheDay();
        Optional<Artist> second = service.getArtistOfTheDay();

        assertTrue(first.isPresent());
        assertTrue(second.isPresent());
        assertEquals(first.get().getId(), second.get().getId());
        verify(artistRepository, times(2)).findArtistAtOffset(2L);
    }
}