package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VenueStatDto {
    private String name;
    private String city;
    private String country;
    private int count;
}
