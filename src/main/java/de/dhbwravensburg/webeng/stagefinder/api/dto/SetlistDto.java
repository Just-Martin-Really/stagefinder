package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SetlistDto {
    private String id;
    private String eventDate;
    private String venueName;
    private String cityName;
    private String countryName;
    private List<String> songs;
    private String url;
}
