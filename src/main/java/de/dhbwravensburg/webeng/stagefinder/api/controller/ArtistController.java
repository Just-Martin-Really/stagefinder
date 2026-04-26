package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistResponse;
import de.dhbwravensburg.webeng.stagefinder.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Locally persisted artists (saved when a user adds a favourite)")
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    @Operation(summary = "List all persisted artists")
    public List<ArtistResponse> getAll() {
        return artistService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a persisted artist by internal ID")
    @ApiResponse(responseCode = "404", description = "Artist not found")
    public ArtistResponse getById(@PathVariable Long id) {
        return artistService.findById(id);
    }
}
