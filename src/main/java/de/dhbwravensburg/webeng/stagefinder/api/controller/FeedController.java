package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.FeedItemDto;
import de.dhbwravensburg.webeng.stagefinder.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Recent setlists from favourited artists")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    @Operation(summary = "Get recent setlists from all favourited artists, sorted by date")
    @ApiResponse(responseCode = "403", description = "Not the owner of this user account")
    public List<FeedItemDto> getFeed(@PathVariable Long userId, Authentication authentication) {
        return feedService.getFeed(userId, authentication.getName());
    }
}
