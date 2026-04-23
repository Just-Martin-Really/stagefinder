package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model;

import lombok.Data;

import java.util.List;

@Data
public class SfmSetlistResponse {
    private int itemsPerPage;
    private int page;
    private int total;
    private List<SfmSetlist> setlist;
}
