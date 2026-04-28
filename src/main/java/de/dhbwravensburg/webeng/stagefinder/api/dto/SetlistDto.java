package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Builder
public class SetlistDto {
    public static final DateTimeFormatter EVENT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private String id;
    private String eventDate;
    private String venueName;
    private String cityName;
    private String countryName;
    private List<String> songs;
    private String url;
}
