package com.deepak.music.artist;

import java.util.UUID;
import com.deepak.music.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class ArtistControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Test
    void testCreateArtistSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pink Floyd\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Pink Floyd"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void testCreateArtistValidationError() throws Exception {
        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetArtistSuccess() throws Exception {
        Artist artist = new Artist();
        artist.setName("Led Zeppelin");
        Artist saved = artistRepository.save(artist);

        mockMvc.perform(get("/api/v1/artists/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Led Zeppelin"))
                .andExpect(jsonPath("$.id").value(saved.getId().toString()));
    }

    @Test
    void testGetArtistNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/artists/" + nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testListArtists() throws Exception {
        Artist artist1 = new Artist();
        artist1.setName("Queen");
        Artist artist2 = new Artist();
        artist2.setName("The Who");

        artistRepository.save(artist1);
        artistRepository.save(artist2);

        mockMvc.perform(get("/api/v1/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)));
    }
}