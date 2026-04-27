package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SongStatDto {
    private String name;
    private int count;
}
