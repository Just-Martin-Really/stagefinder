package de.dhbwravensburg.webeng.stagefinder.service;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ArtistService artistService;

    public List<FavoriteResponse> findByUser(Long userId) {
        getUserOrThrow(userId);
        return favoriteRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public FavoriteResponse add(Long userId, FavoriteRequest request) {
        User user = getUserOrThrow(userId);
        // Artist is resolved via setlist.fm in M3; for now we require it to already exist locally.
        Artist artist = artistService.findOrCreate(request.getMbid(), request.getMbid(), null, null);

        if (favoriteRepository.existsByUserIdAndArtistId(userId, artist.getId())) {
            throw new ConflictException("Artist already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .artist(artist)
                .note(request.getNote())
                .build();
        return toResponse(favoriteRepository.save(favorite));
    }

    @Transactional
    public FavoriteResponse updateNote(Long userId, Long favoriteId, FavoriteNoteRequest request) {
        getUserOrThrow(userId);
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Favorite not found: " + favoriteId));
        favorite.setNote(request.getNote());
        return toResponse(favoriteRepository.save(favorite));
    }

    @Transactional
    public void remove(Long userId, Long favoriteId) {
        getUserOrThrow(userId);
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .filter(f -> f.getUser().getId().equals(userId))
                .orElseThrow(() -> new NotFoundException("Favorite not found: " + favoriteId));
        favoriteRepository.delete(favorite);
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private FavoriteResponse toResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .userId(favorite.getUser().getId())
                .artist(artistService.toResponse(favorite.getArtist()))
                .note(favorite.getNote())
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
