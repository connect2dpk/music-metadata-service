package com.deepak.music.artist.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateArtistRequest(
        @NotBlank(message = "Artist name cannot be blank")
        String name
) {
}
