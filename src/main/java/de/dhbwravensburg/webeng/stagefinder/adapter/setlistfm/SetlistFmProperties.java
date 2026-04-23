package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "setlistfm")
@Data
public class SetlistFmProperties {
    private String baseUrl = "https://api.setlist.fm";
    private String apiVersion = "1.0";
    private String apiKey = "";
    private int timeoutSeconds = 10;
}
