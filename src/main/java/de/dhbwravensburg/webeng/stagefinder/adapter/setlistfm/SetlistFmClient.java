package de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtistSearchResponse;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmSetlistResponse;
import de.dhbwravensburg.webeng.stagefinder.api.exception.ExternalServiceException;
import de.dhbwravensburg.webeng.stagefinder.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class SetlistFmClient {

    private final RestClient restClient;
    private final String apiVersion;

    public SetlistFmClient(SetlistFmProperties props) {
        this.apiVersion = props.getApiVersion();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(props.getTimeoutSeconds()))
                .build();

        this.restClient = RestClient.builder()
                .baseUrl(props.getBaseUrl())
                .requestFactory(new JdkClientHttpRequestFactory(httpClient))
                .defaultHeader("x-api-key", props.getApiKey())
                .defaultHeader("Accept", "application/json")
                .build();
    }

    public List<SfmArtist> searchArtists(String artistName, int page) {
        log.debug("Searching setlist.fm for artist: {}", artistName);
        try {
            SfmArtistSearchResponse response = restClient.get()
                    .uri("/rest/{version}/search/artists?artistName={name}&p={page}",
                            apiVersion, artistName, page)
                    .retrieve()
                    .body(SfmArtistSearchResponse.class);
            if (response == null || response.getArtist() == null) {
                return List.of();
            }
            return response.getArtist();
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("setlist.fm error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("setlist.fm is unreachable: " + e.getMessage());
        }
    }

    public SfmArtist getArtist(String mbid) {
        log.debug("Fetching artist from setlist.fm: {}", mbid);
        try {
            return restClient.get()
                    .uri("/rest/{version}/artist/{mbid}", apiVersion, mbid)
                    .retrieve()
                    .body(SfmArtist.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Artist not found on setlist.fm: " + mbid);
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("setlist.fm error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("setlist.fm is unreachable: " + e.getMessage());
        }
    }

    public SfmSetlistResponse getSetlists(String mbid, int page) {
        log.debug("Fetching setlists from setlist.fm for mbid: {}", mbid);
        try {
            SfmSetlistResponse response = restClient.get()
                    .uri("/rest/{version}/artist/{mbid}/setlists?p={page}", apiVersion, mbid, page)
                    .retrieve()
                    .body(SfmSetlistResponse.class);
            return response != null ? response : new SfmSetlistResponse();
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("No setlists found for artist: " + mbid);
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("setlist.fm error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("setlist.fm is unreachable: " + e.getMessage());
        }
    }
}
