package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.api.dto.UserRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserResponse;
import de.dhbwravensburg.webeng.stagefinder.api.exception.ConflictException;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.User;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void create_succeeds() {
        UserRequest req = new UserRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");

        User saved = User.builder()
                .id(1L).username("alice").email("alice@example.com")
                .createdAt(LocalDateTime.now()).build();

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(saved);

        UserResponse response = userService.create(req);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("alice");
    }

    @Test
    void create_duplicateUsername_throwsConflict() {
        UserRequest req = new UserRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");

        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("alice");
    }

    @Test
    void create_duplicateEmail_throwsConflict() {
        UserRequest req = new UserRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");

        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("alice@example.com");
    }

    @Test
    void findById_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_succeeds() {
        User existing = User.builder()
                .id(1L).username("alice").email("alice@example.com")
                .createdAt(LocalDateTime.now()).build();

        UserRequest req = new UserRequest();
        req.setUsername("alice2");
        req.setEmail("alice2@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("alice2")).thenReturn(false);
        when(userRepository.existsByEmail("alice2@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(existing);

        UserResponse response = userService.update(1L, req);

        assertThat(response).isNotNull();
        verify(userRepository).save(existing);
    }

    @Test
    void delete_notFound_throwsNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(NotFoundException.class);
    }
}
