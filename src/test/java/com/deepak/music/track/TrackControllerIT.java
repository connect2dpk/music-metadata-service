package com.deepak.music.track;

import com.deepak.music.AbstractIntegrationTest;
import com.deepak.music.artist.Artist;
import com.deepak.music.artist.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TrackControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Test
    void testAddTrackSuccess() throws Exception {
        Artist artist = new Artist();
        artist.setName("Metallica");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(post("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"One\",\"genre\":\"METAL\",\"durationSeconds\":447}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", containsString("/api/v1/artists/" + savedArtist.getId() + "/tracks/")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.artistId").value(savedArtist.getId().toString()))
                .andExpect(jsonPath("$.title").value("One"))
                .andExpect(jsonPath("$.genre").value("METAL"))
                .andExpect(jsonPath("$.durationSeconds").value(447));
    }

    @Test
    void testAddTrackArtistNotFound() throws Exception {
        UUID nonExistentArtistId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/artists/" + nonExistentArtistId + "/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Believer\",\"genre\":\"ROCK\",\"durationSeconds\":204}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"));
    }

    @Test
    void testAddTrackInvalidDuration() throws Exception {
        Artist artist = new Artist();
        artist.setName("Imagine Dragons");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(post("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Believer\",\"genre\":\"ROCK\",\"durationSeconds\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.errors.durationSeconds").value("Duration must be greater than 0 seconds"));
    }

    @Test
    void testAddTrackInvalidGenre() throws Exception {
        Artist artist = new Artist();
        artist.setName("Linkin Park");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(post("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Numb\",\"genre\":\"NOT_A_GENRE\",\"durationSeconds\":187}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.detail").value("Invalid request parameters"));
    }

    @Test
    void testAddTrackInvalidGenreGermanLocale() throws Exception {
        Artist artist = new Artist();
        artist.setName("German Locale Artist");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(post("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .header("Accept-Language", "de")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Numb\",\"genre\":\"NOT_A_GENRE\",\"durationSeconds\":187}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.detail", containsString("Anforderungsparameter")));
    }

    @Test
    void testAddTrackInvalidGenreEnglishLocale() throws Exception {
        Artist artist = new Artist();
        artist.setName("English Locale Artist");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(post("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Numb\",\"genre\":\"NOT_A_GENRE\",\"durationSeconds\":187}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.detail").value("Invalid request parameters"));
    }

    @Test
    void testListTracksSuccess() throws Exception {
        Artist artist = new Artist();
        artist.setName("Nirvana");
        Artist savedArtist = artistRepository.save(artist);

        Track track1 = new Track();
        track1.setArtist(savedArtist);
        track1.setTitle("Smells Like Teen Spirit");
        track1.setGenre(Genre.ROCK);
        track1.setDurationSeconds(301);
        trackRepository.save(track1);

        Track track2 = new Track();
        track2.setArtist(savedArtist);
        track2.setTitle("Come As You Are");
        track2.setGenre(Genre.ROCK);
        track2.setDurationSeconds(219);
        trackRepository.save(track2);

        mockMvc.perform(get("/api/v1/artists/" + savedArtist.getId() + "/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void testListTracksEmptyPage() throws Exception {
        Artist artist = new Artist();
        artist.setName("New Artist No Tracks");
        Artist savedArtist = artistRepository.save(artist);

        mockMvc.perform(get("/api/v1/artists/" + savedArtist.getId() + "/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void testListTracksArtistNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/artists/" + nonExistentId + "/tracks"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("ARTIST_NOT_FOUND"));
    }

    @Test
    void testListTracksGenreFilter() throws Exception {
        Artist artist = new Artist();
        artist.setName("Genre Mix Artist");
        Artist savedArtist = artistRepository.save(artist);

        Track rockTrack = new Track();
        rockTrack.setArtist(savedArtist);
        rockTrack.setTitle("Rock Song");
        rockTrack.setGenre(Genre.ROCK);
        rockTrack.setDurationSeconds(200);
        trackRepository.save(rockTrack);

        Track popTrack = new Track();
        popTrack.setArtist(savedArtist);
        popTrack.setTitle("Pop Song");
        popTrack.setGenre(Genre.POP);
        popTrack.setDurationSeconds(180);
        trackRepository.save(popTrack);

        mockMvc.perform(get("/api/v1/artists/" + savedArtist.getId() + "/tracks")
                        .param("genre", "ROCK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("Rock Song"))
                .andExpect(jsonPath("$.content[0].genre").value("ROCK"));
    }
}

