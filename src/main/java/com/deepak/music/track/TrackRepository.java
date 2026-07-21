package com.deepak.music.track;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {
    Page<Track> findAllByArtistId(UUID artistId, Pageable pageable);
    Page<Track> findAllByArtistIdAndGenre(UUID artistId, Genre genre, Pageable pageable);
}
