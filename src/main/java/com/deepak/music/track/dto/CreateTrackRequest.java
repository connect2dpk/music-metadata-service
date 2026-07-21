package com.deepak.music.track.dto;

import com.deepak.music.track.Genre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateTrackRequest(
        @NotBlank(message = "Track title cannot be blank")
        @Size(max = 255, message = "Track title must be at most 255 characters")
        String title,

        @NotNull(message = "Genre is required")
        Genre genre,

        @NotNull(message = "Duration is required")
        @Positive(message = "Duration must be greater than 0 seconds")
        Integer durationSeconds
) {
}
