package com.deepak.music.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception class to be used for optimistic locking failure
 */
public class ArtistConflictException extends ApiException {

    public ArtistConflictException() {
        super("artist.conflict", HttpStatus.CONFLICT.value(), "ARTIST_CONFLICT");
    }
}
