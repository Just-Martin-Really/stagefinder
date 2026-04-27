package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ArtistStatsDto {
    private String mbid;
    private String name;
    private int totalShows;
    private int totalSongPlays;
    private String oldestShowDate;
    private String newestShowDate;
    private List<SongStatDto> topSongs;
    private List<VenueStatDto> topVenues;
}
