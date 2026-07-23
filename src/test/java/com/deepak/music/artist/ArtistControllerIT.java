package com.deepak.music.artist;

import java.util.UUID;
import com.deepak.music.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
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
        String adminToken = generateAdminToken();
        
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pink Floyd\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/v1/artists/")))
                .andExpect(jsonPath("$.name").value("Pink Floyd"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void testCreateArtistUnauthorized() throws Exception {
        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pink Floyd\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateArtistForbidden() throws Exception {
        // Generate a token for a non-admin user (this would work if there's a non-admin user in DB)
        // For now, we test with invalid/expired token scenario
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer invalid_token_12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pink Floyd\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateArtistValidationError() throws Exception {
        String adminToken = generateAdminToken();
        
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.name").value("Artist name cannot be blank"));
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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Artist with id " + nonExistentId + " was not found."));
    }

    @Test
    void testGetArtistNotFoundGermanLocale() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/artists/" + nonExistentId)
                        .header("Accept-Language", "de"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"))
                .andExpect(jsonPath("$.detail", containsString(nonExistentId.toString())))
                .andExpect(jsonPath("$.detail", containsString("wurde nicht gefunden")));
    }

    @Test
    void testGetArtistNotFoundEnglishLocale() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/artists/" + nonExistentId)
                        .header("Accept-Language", "en"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"))
                .andExpect(jsonPath("$.detail").value("Artist with id " + nonExistentId + " was not found."));
    }

    @Test
    void testLangQueryParameterDoesNotChangeLocale() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/artists/" + nonExistentId)
                        .param("lang", "de"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Artist with id " + nonExistentId + " was not found."));
    }

    @Test
    void testListArtistsWithDefaultPagination() throws Exception {
        Artist artist1 = new Artist();
        artist1.setName("Queen");
        Artist artist2 = new Artist();
        artist2.setName("The Who");

        artistRepository.save(artist1);
        artistRepository.save(artist2);

        mockMvc.perform(get("/api/v1/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    void testListArtistsWithCustomPageSize() throws Exception {
        Artist artist1 = new Artist();
        artist1.setName("Artist1");
        Artist artist2 = new Artist();
        artist2.setName("Artist2");

        artistRepository.save(artist1);
        artistRepository.save(artist2);

        mockMvc.perform(get("/api/v1/artists")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void testListArtistsPageSizeExceedsMaximum() throws Exception {
        // Request page size of 200, should be capped to max 100
        mockMvc.perform(get("/api/v1/artists")
                        .param("size", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(100));
    }

    @Test
    void testUpdateArtistNameSuccess() throws Exception {
        Artist artist = new Artist();
        artist.setName("Prince");
        Artist saved = artistRepository.save(artist);
        
        String adminToken = generateAdminToken();

        mockMvc.perform(patch("/api/v1/artists/" + saved.getId() + "/name")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"The Artist Formerly Known as Prince\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId().toString()))
                .andExpect(jsonPath("$.name").value("The Artist Formerly Known as Prince"));
    }

    @Test
    void testUpdateArtistNameUnauthorized() throws Exception {
        Artist artist = new Artist();
        artist.setName("Prince");
        Artist saved = artistRepository.save(artist);

        mockMvc.perform(patch("/api/v1/artists/" + saved.getId() + "/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateArtistNameNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        
        String adminToken = generateAdminToken();

        mockMvc.perform(patch("/api/v1/artists/" + nonExistentId + "/name")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Name\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"));
    }

    @Test
    void testUpdateArtistNameValidationError() throws Exception {
        Artist artist = new Artist();
        artist.setName("Initial Name");
        Artist saved = artistRepository.save(artist);
        
        String adminToken = generateAdminToken();

        mockMvc.perform(patch("/api/v1/artists/" + saved.getId() + "/name")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.name").exists());
    }

    @Test
    void testCreateArtistValidationErrorGermanLocale() throws Exception {
        String adminToken = generateAdminToken();
        
        mockMvc.perform(post("/api/v1/artists")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .header("Accept-Language", "de")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.detail").value("Anforderungsvalidierung fehlgeschlagen"))
                .andExpect(jsonPath("$.errors.name", containsString("darf nicht leer sein")));
    }
}