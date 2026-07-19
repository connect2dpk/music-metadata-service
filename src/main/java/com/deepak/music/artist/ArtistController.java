package com.deepak.music.artist;

import com.deepak.music.artist.dto.ArtistResponse;
import com.deepak.music.artist.dto.CreateArtistRequest;
import com.deepak.music.artist.dto.UpdateArtistNameRequest;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping
    public ResponseEntity<ArtistResponse> createArtist(@Valid @RequestBody CreateArtistRequest request) {
        Artist artist = artistService.create(request);
        ArtistResponse artistResponse = ArtistResponse.from(artist);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(artist.getId())
                .toUri();
        return ResponseEntity.created(uri).body(artistResponse);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponse> getArtist(@PathVariable UUID artistId) {
        Artist artist = artistService.getById(artistId);
        ArtistResponse response = ArtistResponse.from(artist);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ArtistResponse>> listArtists(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        Page<Artist> artists = artistService.list(pageable);
        Page<ArtistResponse> response = artists.map(ArtistResponse::from);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{artistId}/name")
    public ResponseEntity<ArtistResponse> updateArtistName(
            @PathVariable UUID artistId,
            @Valid @RequestBody UpdateArtistNameRequest request) {

        Artist artist = artistService.updateName(artistId, request);
        ArtistResponse response = ArtistResponse.from(artist);
        return ResponseEntity.ok(response);
    }
}
