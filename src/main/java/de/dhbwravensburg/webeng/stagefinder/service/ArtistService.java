package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistResponse;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Artist;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.ArtistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArtistService {

    private final ArtistRepository artistRepository;

    public List<ArtistResponse> findAll() {
        return artistRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ArtistResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public ArtistResponse findByMbid(String mbid) {
        return artistRepository.findByMbid(mbid)
                .map(this::toResponse)
                .orElseThrow(() -> new NotFoundException("Artist not found for mbid: " + mbid));
    }

    /**
     * Finds or creates an Artist record by mbid. Used by FavoriteService when
     * adding a favorite — the artist is upserted from setlist.fm data.
     */
    public Artist findOrCreate(String mbid, String name, String sortName, String url) {
        return artistRepository.findByMbid(mbid).orElseGet(() -> {
            Artist artist = Artist.builder()
                    .mbid(mbid)
                    .name(name)
                    .sortName(sortName)
                    .url(url)
                    .build();
            return artistRepository.save(artist);
        });
    }

    public ArtistResponse toResponse(Artist artist) {
        return ArtistResponse.builder()
                .id(artist.getId())
                .mbid(artist.getMbid())
                .name(artist.getName())
                .sortName(artist.getSortName())
                .url(artist.getUrl())
                .build();
    }

    private Artist getOrThrow(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Artist not found: " + id));
    }
}
