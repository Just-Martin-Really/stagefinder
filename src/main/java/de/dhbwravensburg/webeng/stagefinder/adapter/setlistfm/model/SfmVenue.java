package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model;

import lombok.Data;

@Data
public class SfmVenue {
    private String id;
    private String name;
    private SfmCity city;
}
