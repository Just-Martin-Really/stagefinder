package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model;

import lombok.Data;

@Data
public class SfmSetlist {
    private String id;
    /** Format: DD-MM-YYYY as returned by setlist.fm */
    private String eventDate;
    private String lastUpdated;
    private SfmArtist artist;
    private SfmVenue venue;
    private SfmSets sets;
    private String url;
}
