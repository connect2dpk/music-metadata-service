package com.deepak.music.artist;


import com.deepak.music.artist.dto.CreateArtistRequest;
import com.deepak.music.artist.dto.UpdateArtistNameRequest;
import com.deepak.music.common.exception.ArtistNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ArtistService {
    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public Artist create(CreateArtistRequest request) {
        log.info("Creating artist with name='{}'", request.name());
        Artist newArtist = new Artist();
        newArtist.setName(request.name());
        Artist savedArtist = artistRepository.save(newArtist);
        log.info("Created artist id={} name='{}'", savedArtist.getId(), savedArtist.getName());
        return savedArtist;
    }

    public Artist getById(UUID id) {
        log.debug("Fetching artist by id={}", id);
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }

    public Page<Artist> list(Pageable pageable) {
        log.debug("Listing artists page={} size={} sort={}", pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        return artistRepository.findAll(pageable);
    }

    @Transactional
    public Artist updateName(UUID id, UpdateArtistNameRequest request) {
        log.info("Updating artist name for id={} to name='{}'", id, request.name());
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        artist.setName(request.name());
        Artist savedArtist = artistRepository.save(artist);
        log.info("Updated artist id={} name='{}'", savedArtist.getId(), savedArtist.getName());
        return savedArtist;
    }
}
