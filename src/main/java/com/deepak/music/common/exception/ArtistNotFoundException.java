package com.deepak.music.common.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ArtistNotFoundException extends ApiException{

    public ArtistNotFoundException(UUID artistId) {
        super(
                "Artist with id " + artistId + " not found",
                HttpStatus.NOT_FOUND.value(),
                "ARTIST_NOT_FOUND"
        );
    }
}
