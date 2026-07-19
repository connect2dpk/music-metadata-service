package com.deepak.music.artist;


import com.deepak.music.artist.dto.CreateArtistRequest;
import com.deepak.music.artist.dto.UpdateArtistNameRequest;
import com.deepak.music.common.exception.ArtistNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public Artist create(CreateArtistRequest request) {
        Artist newArtist = new Artist();
        newArtist.setName(request.name());
        return artistRepository.save(newArtist);
    }

    public Artist getById(UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
    }

    public Page<Artist> list(Pageable pageable) {
        return artistRepository.findAll(pageable);
    }

    @Transactional
    public Artist updateName(UUID id, UpdateArtistNameRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));

        artist.setName(request.name());
        return artistRepository.save(artist);
    }
}
