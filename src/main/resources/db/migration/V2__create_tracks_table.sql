CREATE TABLE tracks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    artist_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    duration_seconds INTEGER NOT NULL CHECK (duration_seconds > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_tracks_artist
        FOREIGN KEY (artist_id) REFERENCES artists (id) ON DELETE CASCADE,
    CONSTRAINT ck_tracks_genre
        CHECK (genre IN ('POP', 'ROCK', 'HIP_HOP', 'CLASSICAL', 'JAZZ', 'RNB', 'METAL', 'ELECTRONICS'))
);

CREATE INDEX idx_tracks_artist_id ON tracks (artist_id);
CREATE INDEX idx_tracks_created_at ON tracks (created_at);
CREATE INDEX idx_tracks_artist_created ON tracks (artist_id, created_at, id);
