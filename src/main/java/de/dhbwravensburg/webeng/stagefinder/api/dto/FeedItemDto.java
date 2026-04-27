package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedItemDto {
    private String artistName;
    private String artistMbid;
    private String eventDate;
    private String venueName;
    private String cityName;
    private String countryName;
    private int songCount;
    private String url;
}
