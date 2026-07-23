package com.deepak.music.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ArtistNotFoundException extends ApiException{

    public ArtistNotFoundException(UUID artistId) {
        super(
                "artist.not_found",
                HttpStatus.NOT_FOUND.value(),
                "ARTIST_NOT_FOUND",
                artistId
        );
    }
}
