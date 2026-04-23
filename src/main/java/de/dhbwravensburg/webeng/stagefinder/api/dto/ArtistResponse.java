package de.dhbwravensburg.webeng.stagefinder.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ArtistResponse {
    private Long id;
    private String mbid;
    private String name;
    private String sortName;
    private String url;
}
