package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteNoteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteResponse;
import de.dhbwravensburg.webeng.stagefinder.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/favorites")
@RequiredArgsConstructor
@Tag(name = "Favourites", description = "Manage a user's favourite artists")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    @Operation(summary = "List all favourites for a user")
    @ApiResponse(responseCode = "403", description = "Not the owner of this user account")
    public List<FavoriteResponse> getAll(@PathVariable Long userId, Authentication authentication) {
        return favoriteService.findByUser(userId, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a favourite artist by MusicBrainz ID")
    @ApiResponse(responseCode = "201", description = "Favourite created")
    @ApiResponse(responseCode = "409", description = "Artist already in favourites")
    public FavoriteResponse add(@PathVariable Long userId,
                                @Valid @RequestBody FavoriteRequest request,
                                Authentication authentication) {
        return favoriteService.add(userId, request, authentication.getName());
    }

    @PatchMapping("/{favoriteId}")
    @Operation(summary = "Update the personal note on a favourite")
    @ApiResponse(responseCode = "404", description = "Favourite not found")
    public FavoriteResponse updateNote(@PathVariable Long userId,
                                       @PathVariable Long favoriteId,
                                       @Valid @RequestBody FavoriteNoteRequest request,
                                       Authentication authentication) {
        return favoriteService.updateNote(userId, favoriteId, request, authentication.getName());
    }

    @DeleteMapping("/{favoriteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a favourite")
    @ApiResponse(responseCode = "204", description = "Removed")
    @ApiResponse(responseCode = "404", description = "Favourite not found")
    public void remove(@PathVariable Long userId,
                       @PathVariable Long favoriteId,
                       Authentication authentication) {
        favoriteService.remove(userId, favoriteId, authentication.getName());
    }
}
