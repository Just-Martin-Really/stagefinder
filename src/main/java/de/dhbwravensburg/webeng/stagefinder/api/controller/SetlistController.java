package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmService;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.api.dto.ArtistResponse;
import de.dhbwravensburg.webeng.stagefinder.api.dto.SetlistDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setlists")
@RequiredArgsConstructor
@Validated
@Tag(name = "Setlists", description = "Artist search and setlist data from setlist.fm")
public class SetlistController {

    private final SetlistFmService setlistFmService;

    @GetMapping("/search")
    @Operation(summary = "Search for artists on setlist.fm")
    @ApiResponse(responseCode = "400", description = "Query is blank")
    @ApiResponse(responseCode = "502", description = "setlist.fm upstream error")
    public List<ArtistResponse> searchArtists(
            @Parameter(description = "Artist name to search for") @RequestParam @NotBlank String q,
            @Parameter(description = "Result page (1-based)") @RequestParam(defaultValue = "1") int page) {
        return setlistFmService.searchArtists(q, page).stream()
                .map(sfm -> ArtistResponse.builder()
                        .mbid(sfm.getMbid())
                        .name(sfm.getName())
                        .sortName(sfm.getSortName())
                        .url(sfm.getUrl())
                        .build())
                .toList();
    }

    @GetMapping("/{mbid}")
    @Operation(summary = "Get setlists for an artist by MusicBrainz ID")
    @ApiResponse(responseCode = "404", description = "Artist not found on setlist.fm")
    @ApiResponse(responseCode = "502", description = "setlist.fm upstream error")
    public List<SetlistDto> getSetlists(
            @Parameter(description = "MusicBrainz artist ID") @PathVariable String mbid,
            @Parameter(description = "Result page (1-based)") @RequestParam(defaultValue = "1") int page) {
        return setlistFmService.getSetlists(mbid, page);
    }
}
