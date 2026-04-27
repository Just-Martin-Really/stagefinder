package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FeedItemDto;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Artist;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Favorite;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.User;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.FavoriteRepository;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock FavoriteRepository favoriteRepository;
    @Mock UserRepository userRepository;
    @Mock SetlistFmService setlistFmService;

    @InjectMocks
    FeedService feedService;

    private User stubUser() {
        return User.builder().id(1L).username("alice").email("alice@example.com")
                .passwordHash("$2a$hashed").createdAt(LocalDateTime.now()).build();
    }

    private Artist stubArtist(String mbid, String name) {
        return Artist.builder().id(10L).mbid(mbid).name(name).build();
    }

    @Test
    void getFeed_returnsSortedItems() {
        User user = stubUser();
        Artist a1 = stubArtist("mbid-1", "Metallica");
        Artist a2 = stubArtist("mbid-2", "Radiohead");

        Favorite f1 = Favorite.builder().id(1L).user(user).artist(a1).build();
        Favorite f2 = Favorite.builder().id(2L).user(user).artist(a2).build();

        SetlistDto older = SetlistDto.builder().id("s1").eventDate("10-01-2024")
                .venueName("Arena").cityName("Berlin").songs(List.of("One", "Master")).build();
        SetlistDto newer = SetlistDto.builder().id("s2").eventDate("20-03-2024")
                .venueName("O2").cityName("London").songs(List.of("Creep")).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserId(1L)).thenReturn(List.of(f1, f2));
        when(setlistFmService.getSetlists("mbid-1", 1)).thenReturn(List.of(older));
        when(setlistFmService.getSetlists("mbid-2", 1)).thenReturn(List.of(newer));

        List<FeedItemDto> feed = feedService.getFeed(1L, "alice");

        assertThat(feed).hasSize(2);
        assertThat(feed.get(0).getArtistName()).isEqualTo("Radiohead");
        assertThat(feed.get(1).getArtistName()).isEqualTo("Metallica");
        assertThat(feed.get(0).getSongCount()).isEqualTo(1);
        assertThat(feed.get(1).getSongCount()).isEqualTo(2);
    }

    @Test
    void getFeed_skipsArtistWhenSetlistFmThrows() {
        User user = stubUser();
        Artist a1 = stubArtist("mbid-1", "Metallica");
        Favorite f1 = Favorite.builder().id(1L).user(user).artist(a1).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.findByUserId(1L)).thenReturn(List.of(f1));
        when(setlistFmService.getSetlists("mbid-1", 1)).thenThrow(new RuntimeException("upstream down"));

        List<FeedItemDto> feed = feedService.getFeed(1L, "alice");

        assertThat(feed).isEmpty();
    }

    @Test
    void getFeed_wrongOwner_throwsAccessDenied() {
        User user = stubUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> feedService.getFeed(1L, "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getFeed_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> feedService.getFeed(99L, "alice"))
                .isInstanceOf(NotFoundException.class);
    }
}
