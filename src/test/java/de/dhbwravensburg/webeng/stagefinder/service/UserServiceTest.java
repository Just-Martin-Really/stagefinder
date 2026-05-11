package de.dhbwravensburg.webeng.stagefinder.service;

import de.dhbwravensburg.webeng.stagefinder.api.dto.UserRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserResponse;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserUpdateRequest;
import de.dhbwravensburg.webeng.stagefinder.api.exception.ConflictException;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import de.dhbwravensburg.webeng.stagefinder.domain.entity.User;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    private UserRequest validRequest(String username, String email) {
        UserRequest req = new UserRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword("securepass");
        return req;
    }

    private UserUpdateRequest updateRequest(String username, String email, String password) {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setUsername(username);
        req.setEmail(email);
        req.setPassword(password);
        return req;
    }

    private User savedUser(Long id, String username, String email) {
        return User.builder()
                .id(id).username(username).email(email)
                .passwordHash("$2a$hashed")
                .createdAt(LocalDateTime.now()).build();
    }

    @Test
    void create_succeeds() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$hashed");
        when(userRepository.save(any())).thenReturn(savedUser(1L, "alice", "alice@example.com"));

        UserResponse response = userService.create(validRequest("alice", "alice@example.com"));

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("alice");
        verify(passwordEncoder).encode("securepass");
    }

    @Test
    void create_duplicateUsername_throwsConflict() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(validRequest("alice", "alice@example.com")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("alice");
    }

    @Test
    void create_duplicateEmail_throwsConflict() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.create(validRequest("alice", "alice@example.com")))
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
    void update_withPassword_rotatesHash() {
        User existing = savedUser(1L, "alice", "alice@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("alice2")).thenReturn(false);
        when(userRepository.existsByEmail("alice2@example.com")).thenReturn(false);
        when(passwordEncoder.encode("newpass123")).thenReturn("$2a$rotated");
        when(userRepository.save(any())).thenReturn(existing);

        UserResponse response = userService.update(
                1L, updateRequest("alice2", "alice2@example.com", "newpass123"), "alice");

        assertThat(response).isNotNull();
        assertThat(existing.getPasswordHash()).isEqualTo("$2a$rotated");
        verify(passwordEncoder).encode("newpass123");
    }

    @Test
    void update_withoutPassword_keepsExistingHash() {
        User existing = savedUser(1L, "alice", "alice@example.com");
        String originalHash = existing.getPasswordHash();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("alice2@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(existing);

        UserResponse response = userService.update(
                1L, updateRequest("alice", "alice2@example.com", null), "alice");

        assertThat(response).isNotNull();
        assertThat(existing.getEmail()).isEqualTo("alice2@example.com");
        assertThat(existing.getPasswordHash()).isEqualTo(originalHash);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void update_blankPassword_keepsExistingHash() {
        User existing = savedUser(1L, "alice", "alice@example.com");
        String originalHash = existing.getPasswordHash();
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenReturn(existing);

        userService.update(1L, updateRequest("alice", "alice@example.com", "   "), "alice");

        assertThat(existing.getPasswordHash()).isEqualTo(originalHash);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void update_wrongOwner_throwsAccessDenied() {
        User existing = savedUser(1L, "alice", "alice@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.update(
                1L, updateRequest("alice", "alice@example.com", "securepass"), "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void delete_wrongOwner_throwsAccessDenied() {
        User existing = savedUser(1L, "alice", "alice@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.delete(1L, "bob"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void delete_notFound_throwsNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(99L, "anyone"))
                .isInstanceOf(NotFoundException.class);
    }
}
