package de.dhbwravensburg.webeng.stagefinder.api;

import de.dhbwravensburg.webeng.stagefinder.api.dto.UserRequest;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.FavoriteRepository;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerIT {

    @Autowired WebApplicationContext context;
    @Autowired UserRepository userRepository;
    @Autowired FavoriteRepository favoriteRepository;
    @Autowired ObjectMapper objectMapper;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        favoriteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createUser_validRequest_returns201() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("testuser");
        req.setEmail("test@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void createUser_missingEmail_returns400() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("testuser");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    void createUser_usernameTooShort_returns400() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("ab");
        req.setEmail("test@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_duplicateUsername_returns409() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        UserRequest dup = new UserRequest();
        dup.setUsername("alice");
        dup.setEmail("other@example.com");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dup)))
                .andExpect(status().isConflict());
    }

    @Test
    void getUser_notFound_returns404() throws Exception {
        mockMvc.perform(get("/api/users/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_returns204_thenNotFound() throws Exception {
        UserRequest req = new UserRequest();
        req.setUsername("todelete");
        req.setEmail("todelete@example.com");

        String body = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(body).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isNotFound());
    }
}
