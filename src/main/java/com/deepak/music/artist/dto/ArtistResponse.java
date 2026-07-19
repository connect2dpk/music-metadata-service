package com.deepak.music.artist.dto;

import com.deepak.music.artist.Artist;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ArtistResponse(
    UUID id,
    String name,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {
    public static ArtistResponse from(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }
}
