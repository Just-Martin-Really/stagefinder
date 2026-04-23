package de.dhbwravensburg.webeng.stagefinder.api.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FavoriteNoteRequest {

    @Size(max = 500)
    private String note;
}
