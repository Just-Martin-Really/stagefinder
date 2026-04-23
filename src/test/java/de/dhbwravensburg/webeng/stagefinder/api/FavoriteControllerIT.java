package de.dhbwravensburg.webeng.stagefinder.api;

import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.SetlistFmClient;
import de.dhbwravensburg.webeng.stagefinder.adapter.setlistfm.model.SfmArtist;
import de.dhbwravensburg.webeng.stagefinder.api.dto.FavoriteRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserRequest;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.ArtistRepository;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.FavoriteRepository;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class FavoriteControllerIT {

    @Autowired
    WebApplicationContext context;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FavoriteRepository favoriteRepository;
    @Autowired
    ArtistRepository artistRepository;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    SetlistFmClient setlistFmClient;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        favoriteRepository.deleteAll();
        artistRepository.deleteAll();
        userRepository.deleteAll();

        SfmArtist sfmArtist = new SfmArtist();
        sfmArtist.setMbid("abc-123");
        sfmArtist.setName("Metallica");
        sfmArtist.setSortName("Metallica");
        sfmArtist.setUrl("https://www.setlist.fm/setlists/metallica.html");

        when(setlistFmClient.getArtist(anyString())).thenReturn(sfmArtist);
    }

    private Long createUser(String username, String email) throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername(username);
        req.setEmail(email);

        String body = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(body).get("id").asLong();
    }

    @Test
    void addFavorite_returns201WithArtistData() throws Exception {
        Long userId = createUser("fan1", "fan1@example.com");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");
        req.setNote("Classic!");

        mockMvc.perform(post("/api/users/" + userId + "/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artist.mbid").value("abc-123"))
                .andExpect(jsonPath("$.artist.name").value("Metallica"))
                .andExpect(jsonPath("$.note").value("Classic!"));
    }

    @Test
    void addFavorite_duplicate_returns409() throws Exception {
        Long userId = createUser("fan2", "fan2@example.com");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");

        mockMvc.perform(post("/api/users/" + userId + "/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/users/" + userId + "/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    void getFavorites_returnsUserFavorites() throws Exception {
        Long userId = createUser("fan3", "fan3@example.com");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");

        mockMvc.perform(post("/api/users/" + userId + "/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/users/" + userId + "/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].artist.name").value("Metallica"));
    }

    @Test
    void removeFavorite_returns204() throws Exception {
        Long userId = createUser("fan4", "fan4@example.com");

        FavoriteRequest req = new FavoriteRequest();
        req.setMbid("abc-123");

        String body = mockMvc.perform(post("/api/users/" + userId + "/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long favoriteId = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + userId + "/favorites/" + favoriteId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + userId + "/favorites"))
                .andExpect(jsonPath("$.length()").value(0));
    }
}
