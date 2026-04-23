package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistResponse;
import de.dhbwravensburg.webeng.stagefinder.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @GetMapping
    public List<ArtistResponse> getAll() {
        return artistService.findAll();
    }

    @GetMapping("/{id}")
    public ArtistResponse getById(@PathVariable Long id) {
        return artistService.findById(id);
    }
}
