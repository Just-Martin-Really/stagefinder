package de.dhbwravensburg.webeng.stagefinder.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FavoriteRequest {

    @NotBlank
    private String mbid;

    @Size(max = 500)
    private String note;
}
