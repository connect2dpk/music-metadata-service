package com.deepak.music.track.dto;


import com.deepak.music.track.Track;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackResponse(
        UUID id,
        UUID artistId,
        String title,
        String genre,
        Integer durationSeconds,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static TrackResponse from(Track track) {
        return new TrackResponse(
                track.getId(),
                track.getArtist().getId(),
                track.getTitle(),
                track.getGenre().name(),
                track.getDurationSeconds(),
                track.getCreatedAt(),
                track.getUpdatedAt()
        );
    }
}
