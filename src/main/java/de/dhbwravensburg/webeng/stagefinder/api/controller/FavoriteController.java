package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteNoteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteResponse;
import de.dhbwravensburg.webeng.stagefinder.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public List<FavoriteResponse> getAll(@PathVariable Long userId, Authentication authentication) {
        return favoriteService.findByUser(userId, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@PathVariable Long userId,
                                @Valid @RequestBody FavoriteRequest request,
                                Authentication authentication) {
        return favoriteService.add(userId, request, authentication.getName());
    }

    @PatchMapping("/{favoriteId}")
    public FavoriteResponse updateNote(@PathVariable Long userId,
                                       @PathVariable Long favoriteId,
                                       @Valid @RequestBody FavoriteNoteRequest request,
                                       Authentication authentication) {
        return favoriteService.updateNote(userId, favoriteId, request, authentication.getName());
    }

    @DeleteMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long userId,
                       @PathVariable Long favoriteId,
                       Authentication authentication) {
        favoriteService.remove(userId, favoriteId, authentication.getName());
    }
}
