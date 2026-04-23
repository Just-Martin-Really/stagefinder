package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistResponse;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setlists")
@RequiredArgsConstructor
@Validated
public class SetlistController {

    private final SetlistFmService setlistFmService;

    @GetMapping("/search")
    public List<ArtistResponse> searchArtists(
            @RequestParam @NotBlank String q,
            @RequestParam(defaultValue = "1") int page) {
        return setlistFmService.searchArtists(q, page);
    }

    @GetMapping("/{mbid}")
    public List<SetlistDto> getSetlists(
            @PathVariable String mbid,
            @RequestParam(defaultValue = "1") int page) {
        return setlistFmService.getSetlists(mbid, page);
    }
}
