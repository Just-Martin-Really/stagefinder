package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model;

import lombok.Data;

import java.util.List;

@Data
public class SfmSet {
    private String name;
    private List<SfmSong> song;
}
