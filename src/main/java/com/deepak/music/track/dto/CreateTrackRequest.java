package com.deepak.music.track.dto;

import com.deepak.music.track.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTrackRequest(
        @NotBlank(message = "{validation.track.title.required}")
        @Size(max = 255, message = "{validation.track.title.max}")
        String title,

        @NotNull(message = "{validation.track.genre.required}")
        Genre genre,

        @NotNull(message = "{validation.track.duration.required}")
        @Positive(message = "{validation.track.duration.positive}")
        Integer durationSeconds
) {
}
