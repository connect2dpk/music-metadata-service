package com.deepak.music.artist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    @Query(value = """
            SELECT *
            FROM artists
            ORDER BY created_at ASC, id ASC
            LIMIT 1 OFFSET :offset
            """, nativeQuery = true)
    Optional<Artist> findArtistAtOffset(@Param("offset") long offset);

}
