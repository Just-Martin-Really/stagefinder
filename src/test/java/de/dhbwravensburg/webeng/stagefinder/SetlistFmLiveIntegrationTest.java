package de.dhbwravensburg.webeng.stagefinder;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class SetlistFmLiveIntegrationTest {

    @Test
    void canReachSetlistFmWithProvidedApiKey() throws IOException, InterruptedException {
        String liveTestsEnabled = System.getenv("RUN_SETLISTFM_LIVE_TESTS");
        assumeTrue("true".equalsIgnoreCase(liveTestsEnabled),
                "Live setlist.fm tests are disabled. Set RUN_SETLISTFM_LIVE_TESTS=true to enable.");

        String apiKey = System.getenv("SETLISTFM_API_KEY");
        assumeTrue(apiKey != null && !apiKey.isBlank(),
                "SETLISTFM_API_KEY must be set to run live setlist.fm tests.");

        String baseUrl = System.getenv().getOrDefault("SETLISTFM_BASE_URL", "https://api.setlist.fm");
        String apiVersion = System.getenv().getOrDefault("SETLISTFM_API_VERSION", "1.0");
        String requestUrl = String.format("%s/rest/%s/search/artists?artistName=metallica&p=1", baseUrl, apiVersion);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("x-api-key", apiKey)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                () -> "Unexpected setlist.fm status code: " + response.statusCode());

        String contentType = response.headers().firstValue("content-type").orElse("");
        assertTrue(contentType.contains("application/json"),
                () -> "Expected JSON response but got: " + contentType);

        assertTrue(response.body() != null && !response.body().isBlank(), "Response body should not be blank");
    }
}

