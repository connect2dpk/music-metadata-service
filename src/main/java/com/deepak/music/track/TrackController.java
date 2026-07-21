package com.deepak.music.track;

import com.deepak.music.track.dto.CreateTrackRequest;
import com.deepak.music.track.dto.TrackResponse;
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
public class TrackController {
    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/api/v1/artists/{artistId}/tracks")
    public ResponseEntity<TrackResponse> addTrack(
            @PathVariable UUID artistId,
            @RequestBody @Valid CreateTrackRequest request
            ) {

        Track track = trackService.addTrack(artistId, request);
        TrackResponse trackResponse = TrackResponse.from(track);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{trackId}")
                .buildAndExpand(track.getId())
                .toUri();

        return ResponseEntity.created(uri).body(trackResponse);
    }

    @GetMapping("/api/v1/artists/{artistId}/tracks")
    public ResponseEntity<Page<TrackResponse>> listTracks(
            @PathVariable UUID artistId,
            @RequestParam(required = false) Genre genre,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        Page<Track> tracks = trackService.listTracks(artistId, genre, pageable);
        Page<TrackResponse> response = tracks.map(TrackResponse::from);
        return ResponseEntity.ok(response);
    }

}
