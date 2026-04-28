package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmSet;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmSetlist;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmSetlistResponse;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SetlistFmService {

    private final SetlistFmClient client;

    public List<SfmArtist> searchArtists(String query, int page) {
        return client.searchArtists(query, page);
    }

    public SfmArtist getArtist(String mbid) {
        return client.getArtist(mbid);
    }

    public List<SetlistDto> getSetlists(String mbid, int page) {
        SfmSetlistResponse response = client.getSetlists(mbid, page);
        if (response.getSetlist() == null) {
            return List.of();
        }
        return response.getSetlist().stream()
                .map(this::toSetlistDto)
                .toList();
    }

    private SetlistDto toSetlistDto(SfmSetlist s) {
        String venueName = Optional.ofNullable(s.getVenue()).map(v -> v.getName()).orElse(null);
        String cityName = Optional.ofNullable(s.getVenue())
                .map(v -> v.getCity())
                .map(c -> c.getName())
                .orElse(null);
        String countryName = Optional.ofNullable(s.getVenue())
                .map(v -> v.getCity())
                .map(c -> c.getCountry())
                .map(co -> co.getName())
                .orElse(null);

        List<String> songs = Optional.ofNullable(s.getSets())
                .map(sets -> sets.getSet())
                .orElse(Collections.emptyList())
                .stream()
                .filter(set -> set.getSong() != null)
                .flatMap(set -> set.getSong().stream())
                .map(song -> song.getName())
                .toList();

        return SetlistDto.builder()
                .id(s.getId())
                .eventDate(s.getEventDate())
                .venueName(venueName)
                .cityName(cityName)
                .countryName(countryName)
                .songs(songs)
                .url(s.getUrl())
                .build();
    }
}
