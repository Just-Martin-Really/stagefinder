package de.dhbwravensburg.webeng.stagefinder.adapter;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmClient;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.*;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetlistFmServiceTest {

    @Mock
    SetlistFmClient client;

    @InjectMocks
    SetlistFmService setlistFmService;

    @Test
    void searchArtists_returnsClientResults() {
        SfmArtist sfm = new SfmArtist();
        sfm.setMbid("abc-123");
        sfm.setName("Metallica");

        when(client.searchArtists("Metallica", 1)).thenReturn(List.of(sfm));

        List<SfmArtist> results = setlistFmService.searchArtists("Metallica", 1);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getMbid()).isEqualTo("abc-123");
        assertThat(results.get(0).getName()).isEqualTo("Metallica");
    }

    @Test
    void searchArtists_emptyResult_returnsEmptyList() {
        when(client.searchArtists("Unknown Artist XYZ", 1)).thenReturn(List.of());

        List<SfmArtist> results = setlistFmService.searchArtists("Unknown Artist XYZ", 1);

        assertThat(results).isEmpty();
    }

    @Test
    void getSetlists_mapsSongsAndVenueCorrectly() {
        SfmSong song1 = new SfmSong(); song1.setName("Enter Sandman");
        SfmSong song2 = new SfmSong(); song2.setName("Master of Puppets");

        SfmSet set = new SfmSet();
        set.setSong(List.of(song1, song2));

        SfmSets sets = new SfmSets();
        sets.setSet(List.of(set));

        SfmCountry country = new SfmCountry(); country.setName("Germany");
        SfmCity city = new SfmCity(); city.setName("Berlin"); city.setCountry(country);
        SfmVenue venue = new SfmVenue(); venue.setName("Waldbühne"); venue.setCity(city);

        SfmArtist artist = new SfmArtist(); artist.setMbid("abc-123"); artist.setName("Metallica");

        SfmSetlist setlist = new SfmSetlist();
        setlist.setId("setlist-1");
        setlist.setEventDate("15-07-2024");
        setlist.setArtist(artist);
        setlist.setVenue(venue);
        setlist.setSets(sets);

        SfmSetlistResponse response = new SfmSetlistResponse();
        response.setSetlist(List.of(setlist));

        when(client.getSetlists("abc-123", 1)).thenReturn(response);

        List<SetlistDto> dtos = setlistFmService.getSetlists("abc-123", 1);

        assertThat(dtos).hasSize(1);
        SetlistDto dto = dtos.get(0);
        assertThat(dto.getEventDate()).isEqualTo("15-07-2024");
        assertThat(dto.getVenueName()).isEqualTo("Waldbühne");
        assertThat(dto.getCityName()).isEqualTo("Berlin");
        assertThat(dto.getCountryName()).isEqualTo("Germany");
        assertThat(dto.getSongs()).containsExactly("Enter Sandman", "Master of Puppets");
    }

    @Test
    void getSetlists_nullSetlist_returnsEmptyList() {
        SfmSetlistResponse response = new SfmSetlistResponse();
        response.setSetlist(null);

        when(client.getSetlists("abc-123", 1)).thenReturn(response);

        List<SetlistDto> dtos = setlistFmService.getSetlists("abc-123", 1);

        assertThat(dtos).isEmpty();
    }
}
