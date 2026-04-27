package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistStatsDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Artist;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistStatsServiceTest {

    @Mock ArtistRepository artistRepository;
    @Mock SetlistFmService setlistFmService;

    @InjectMocks
    ArtistStatsService artistStatsService;

    @Test
    void getStats_usesLocalNameWhenAvailable() {
        Artist local = Artist.builder().id(1L).mbid("mbid-1").name("Metallica").build();
        when(artistRepository.findByMbid("mbid-1")).thenReturn(Optional.of(local));

        SetlistDto s1 = SetlistDto.builder().eventDate("01-01-2024").venueName("Arena").cityName("Berlin").countryName("Germany")
                .songs(List.of("One", "Master of Puppets", "One")).build();
        SetlistDto s2 = SetlistDto.builder().eventDate("02-01-2024").venueName("Arena").cityName("Berlin").countryName("Germany")
                .songs(List.of("One", "Fade to Black")).build();

        when(setlistFmService.getSetlists("mbid-1", 1)).thenReturn(List.of(s1, s2));
        when(setlistFmService.getSetlists("mbid-1", 2)).thenReturn(List.of());
        when(setlistFmService.getSetlists("mbid-1", 3)).thenReturn(List.of());

        ArtistStatsDto stats = artistStatsService.getStats("mbid-1");

        assertThat(stats.getName()).isEqualTo("Metallica");
        assertThat(stats.getTotalShows()).isEqualTo(2);
        assertThat(stats.getTotalSongPlays()).isEqualTo(5);
        assertThat(stats.getTopSongs().get(0).getName()).isEqualTo("One");
        assertThat(stats.getTopSongs().get(0).getCount()).isEqualTo(3);
        assertThat(stats.getTopVenues().get(0).getName()).isEqualTo("Arena");
        assertThat(stats.getTopVenues().get(0).getCount()).isEqualTo(2);
        verify(setlistFmService, never()).getArtist(any());
    }

    @Test
    void getStats_fallsBackToSetlistFmForName() {
        when(artistRepository.findByMbid("mbid-2")).thenReturn(Optional.empty());

        SfmArtist sfm = new SfmArtist();
        sfm.setMbid("mbid-2");
        sfm.setName("Radiohead");
        when(setlistFmService.getArtist("mbid-2")).thenReturn(sfm);
        when(setlistFmService.getSetlists(eq("mbid-2"), anyInt())).thenReturn(List.of());

        ArtistStatsDto stats = artistStatsService.getStats("mbid-2");

        assertThat(stats.getName()).isEqualTo("Radiohead");
    }

    @Test
    void getStats_topSongsOrderedByCount() {
        Artist local = Artist.builder().id(1L).mbid("mbid-1").name("Test").build();
        when(artistRepository.findByMbid("mbid-1")).thenReturn(Optional.of(local));

        SetlistDto s = SetlistDto.builder()
                .songs(List.of("A", "B", "B", "C", "C", "C"))
                .venueName("V").cityName("X").countryName("Y").build();
        when(setlistFmService.getSetlists("mbid-1", 1)).thenReturn(List.of(s));
        when(setlistFmService.getSetlists("mbid-1", 2)).thenReturn(List.of());
        when(setlistFmService.getSetlists("mbid-1", 3)).thenReturn(List.of());

        ArtistStatsDto stats = artistStatsService.getStats("mbid-1");

        assertThat(stats.getTopSongs()).extracting("name").containsExactly("C", "B", "A");
        assertThat(stats.getTopSongs()).extracting("count").containsExactly(3, 2, 1);
    }
}
