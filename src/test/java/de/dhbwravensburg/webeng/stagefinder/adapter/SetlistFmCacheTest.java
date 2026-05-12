package de.dhbwravensburg.webeng.stagefinder.adapter;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmClient;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmSetlistResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = "spring.cache.type=caffeine")
class SetlistFmCacheTest {

    @MockitoBean
    SetlistFmClient client;

    @Autowired
    SetlistFmService service;

    @Test
    void getArtist_secondCallHitsCache() {
        SfmArtist artist = new SfmArtist();
        artist.setMbid("mbid-cache-1");
        artist.setName("Cached");

        when(client.getArtist("mbid-cache-1")).thenReturn(artist);

        SfmArtist first = service.getArtist("mbid-cache-1");
        SfmArtist second = service.getArtist("mbid-cache-1");

        assertThat(first.getName()).isEqualTo("Cached");
        assertThat(second).isSameAs(first);
        verify(client, times(1)).getArtist("mbid-cache-1");
    }

    @Test
    void getSetlists_secondCallHitsCache() {
        SfmSetlistResponse response = new SfmSetlistResponse();
        response.setSetlist(List.of());

        when(client.getSetlists("mbid-cache-2", 1)).thenReturn(response);

        List<?> first = service.getSetlists("mbid-cache-2", 1);
        List<?> second = service.getSetlists("mbid-cache-2", 1);

        assertThat(first).isEmpty();
        assertThat(second).isSameAs(first);
        verify(client, times(1)).getSetlists("mbid-cache-2", 1);
    }

    @Test
    void getSetlists_differentPageBypassesCache() {
        SfmSetlistResponse response = new SfmSetlistResponse();
        response.setSetlist(List.of());

        when(client.getSetlists("mbid-cache-3", 1)).thenReturn(response);
        when(client.getSetlists("mbid-cache-3", 2)).thenReturn(response);

        service.getSetlists("mbid-cache-3", 1);
        service.getSetlists("mbid-cache-3", 2);

        verify(client, times(1)).getSetlists("mbid-cache-3", 1);
        verify(client, times(1)).getSetlists("mbid-cache-3", 2);
    }
}
