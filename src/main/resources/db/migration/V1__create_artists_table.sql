CREATE TABLE artists (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_artists_created_at_id ON artists (created_at, id);
CREATE INDEX idx_artists_name ON artists (name);