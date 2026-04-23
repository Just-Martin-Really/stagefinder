package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FavoriteResponse {
    private Long id;
    private Long userId;
    private ArtistResponse artist;
    private String note;
    private LocalDateTime createdAt;
}
