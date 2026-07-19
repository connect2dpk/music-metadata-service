package com.deepak.music.artist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for Updating Artist name
 */
public record UpdateArtistNameRequest(
        @NotBlank(message = "Artist name cannot  be blank")
        @Size(max = 255, message = "Artist name must be at most 255 characters")
        String name
) { }
