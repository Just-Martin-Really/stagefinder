package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.Artist;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.ArtistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    ArtistRepository artistRepository;

    @InjectMocks
    ArtistService artistService;

    @Test
    void findOrCreate_existingArtist_returnsWithoutSaving() {
        Artist existing = Artist.builder()
                .id(1L).mbid("abc-123").name("Metallica").build();

        when(artistRepository.findByMbid("abc-123")).thenReturn(Optional.of(existing));

        Artist result = artistService.findOrCreate("abc-123", "Metallica", null, null);

        assertThat(result.getId()).isEqualTo(1L);
        verify(artistRepository, never()).save(any());
    }

    @Test
    void findOrCreate_newArtist_savesAndReturns() {
        Artist saved = Artist.builder()
                .id(2L).mbid("xyz-456").name("Radiohead").build();

        when(artistRepository.findByMbid("xyz-456")).thenReturn(Optional.empty());
        when(artistRepository.save(any())).thenReturn(saved);

        Artist result = artistService.findOrCreate("xyz-456", "Radiohead", "Radiohead", null);

        assertThat(result.getId()).isEqualTo(2L);
        verify(artistRepository).save(any());
    }

    @Test
    void findById_notFound_throwsNotFoundException() {
        when(artistRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}
