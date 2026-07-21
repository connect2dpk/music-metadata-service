package com.deepak.music.artistoftheday;

import com.deepak.music.AbstractIntegrationTest;
import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ArtistOfTheDayControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    void setUp() {
        artistRepository.deleteAll();
    }

    @Test
    void testArtistOfTheDayNoArtists() throws Exception {
        mockMvc.perform(get("/api/v1/artist-of-the-day"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testArtistOfTheDayReturnsExpectedArtist() throws Exception {
        Artist a1 = new Artist();
        a1.setName("Artist A");
        artistRepository.save(a1);

        Artist a2 = new Artist();
        a2.setName("Artist B");
        artistRepository.save(a2);

        Artist a3 = new Artist();
        a3.setName("Artist C");
        artistRepository.save(a3);

        List<Artist> ordered = artistRepository.findAll(
                Sort.by(Sort.Order.asc("createdAt"), Sort.Order.asc("id"))
        );

        long count = ordered.size();
        long epochDay = LocalDate.now(ZoneOffset.UTC).toEpochDay();
        int offset = (int) Math.floorMod(epochDay, count);

        Artist expected = ordered.get(offset);

        mockMvc.perform(get("/api/v1/artist-of-the-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expected.getId().toString()))
                .andExpect(jsonPath("$.name").value(expected.getName()));
    }

    @Test
    void testArtistOfTheDaySameDayStableResult() throws Exception {
        Artist artist = new Artist();
        artist.setName("Stable Result Artist");
        artistRepository.save(artist);

        MvcResult first = mockMvc.perform(get("/api/v1/artist-of-the-day"))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult second = mockMvc.perform(get("/api/v1/artist-of-the-day"))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(
                first.getResponse().getContentAsString(),
                second.getResponse().getContentAsString()
        );
    }
}