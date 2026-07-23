package com.deepak.music.artist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateArtistRequest(
        @NotBlank(message = "{validation.artist.name.required}")
        @Size(max = 255, message = "{validation.artist.name.max}")
        String name
) {
}
