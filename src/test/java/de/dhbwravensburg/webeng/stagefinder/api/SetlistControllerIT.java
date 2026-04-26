package de.dhbwravensburg.webeng.stagefinder.api;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmClient;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SetlistControllerIT {

    @Autowired WebApplicationContext context;

    @MockitoBean
    SetlistFmClient setlistFmClient;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void searchArtists_returnsMatchingArtists() throws Exception {
        SfmArtist artist = new SfmArtist();
        artist.setMbid("abc-123");
        artist.setName("Metallica");
        artist.setSortName("Metallica");
        artist.setUrl("https://www.setlist.fm/setlists/metallica.html");

        when(setlistFmClient.searchArtists("Metallica", 1)).thenReturn(List.of(artist));

        mockMvc.perform(get("/api/setlists/search").param("q", "Metallica"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].mbid").value("abc-123"))
                .andExpect(jsonPath("$[0].name").value("Metallica"));
    }

    @Test
    void searchArtists_blankQuery_returns400() throws Exception {
        mockMvc.perform(get("/api/setlists/search").param("q", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSetlists_returnsSetlistsForArtist() throws Exception {
        SfmSong song = new SfmSong();
        song.setName("Enter Sandman");

        SfmSet set = new SfmSet();
        set.setSong(List.of(song));

        SfmSets sets = new SfmSets();
        sets.setSet(List.of(set));

        SfmCountry country = new SfmCountry();
        country.setName("Germany");

        SfmCity city = new SfmCity();
        city.setName("Berlin");
        city.setCountry(country);

        SfmVenue venue = new SfmVenue();
        venue.setName("Waldbühne");
        venue.setCity(city);

        SfmArtist artist = new SfmArtist();
        artist.setMbid("abc-123");
        artist.setName("Metallica");

        SfmSetlist setlist = new SfmSetlist();
        setlist.setId("setlist-1");
        setlist.setEventDate("15-07-2024");
        setlist.setArtist(artist);
        setlist.setVenue(venue);
        setlist.setSets(sets);

        SfmSetlistResponse response = new SfmSetlistResponse();
        response.setSetlist(List.of(setlist));

        when(setlistFmClient.getSetlists("abc-123", 1)).thenReturn(response);

        mockMvc.perform(get("/api/setlists/abc-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].venueName").value("Waldbühne"))
                .andExpect(jsonPath("$[0].cityName").value("Berlin"))
                .andExpect(jsonPath("$[0].songs[0]").value("Enter Sandman"));
    }
}
