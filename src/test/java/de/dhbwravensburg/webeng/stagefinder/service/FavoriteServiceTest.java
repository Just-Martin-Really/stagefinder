package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteNoteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteResponse;
import de.dhbwravensburg.webeng.stagefinder.api.exception.ConflictException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock FavoriteRepository favoriteRepository;
    @Mock UserRepository userRepository;
    @Mock ArtistService artistService;
    @Mock SetlistFmService setlistFmService;

    @InjectMocks
    FavoriteService favoriteService;

    private User stubUser() {
        return User.builder().id(1L).username("alice").email("alice@example.com")
                .passwordHash("$2a$hashed").createdAt(LocalDateTime.now()).build();
    }

    private Artist stubArtist() {
        return Artist.builder().id(10L).mbid("abc-123").name("Metallica").build();
    }

    @Test
    void add_succeeds() {
        User user = stubUser();
        Artist artist = stubArtist();

        SfmArtist sfm = new SfmArtist();
        sfm.setMbid("abc-123");
        sfm.setName("Metallica");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");
        req.setNote("love them");

        Favorite saved = Favorite.builder()
                .id(100L).user(user).artist(artist).note("love them")
                .createdAt(LocalDateTime.now()).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(setlistFmService.getArtist("abc-123")).thenReturn(sfm);
        when(artistService.findOrCreate("abc-123", "Metallica", null, null)).thenReturn(artist);
        when(favoriteRepository.existsByUserIdAndArtistId(1L, 10L)).thenReturn(false);
        when(favoriteRepository.save(any())).thenReturn(saved);
        when(artistService.toResponse(artist)).thenCallRealMethod();

        FavoriteResponse response = favoriteService.add(1L, req, "alice");

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getNote()).isEqualTo("love them");
    }

    @Test
    void add_wrongOwner_throwsAccessDenied() {
        User user = stubUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> favoriteService.add(1L, new FavoriteRequest(), "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void add_duplicate_throwsConflict() {
        User user = stubUser();
        Artist artist = stubArtist();

        SfmArtist sfm = new SfmArtist();
        sfm.setMbid("abc-123");
        sfm.setName("Metallica");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(setlistFmService.getArtist("abc-123")).thenReturn(sfm);
        when(artistService.findOrCreate("abc-123", "Metallica", null, null)).thenReturn(artist);
        when(favoriteRepository.existsByUserIdAndArtistId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> favoriteService.add(1L, req, "alice"))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void remove_notFound_throwsNotFoundException() {
        User user = stubUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.remove(1L, 999L, "alice"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateNote_succeeds() {
        User user = stubUser();
        Artist artist = stubArtist();
        Favorite favorite = Favorite.builder()
                .id(100L).user(user).artist(artist).note("old")
                .createdAt(LocalDateTime.now()).build();

        FavoriteNoteRequest req = new FavoriteNoteRequest();
        req.setNote("updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(favoriteRepository.findById(100L)).thenReturn(Optional.of(favorite));
        when(favoriteRepository.save(any())).thenReturn(favorite);
        when(artistService.toResponse(artist)).thenCallRealMethod();

        FavoriteResponse response = favoriteService.updateNote(1L, 100L, req, "alice");

        assertThat(response).isNotNull();
        verify(favoriteRepository).save(favorite);
    }

    @Test
    void findByUser_userNotFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> favoriteService.findByUser(99L, "anyone"))
                .isInstanceOf(NotFoundException.class);
    }
}
