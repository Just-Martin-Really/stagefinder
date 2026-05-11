package de.dhbwravensburg.webeng.stagefinder.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordToStringTest {

    private static final String SECRET = "s3cretP@ssw0rd";

    @Test
    void authRequest_toString_doesNotContainPassword() {
        AuthRequest req = new AuthRequest();
        req.setUsername("alice");
        req.setPassword(SECRET);

        assertThat(req.toString()).doesNotContain(SECRET);
    }

    @Test
    void userRequest_toString_doesNotContainPassword() {
        UserRequest req = new UserRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword(SECRET);

        assertThat(req.toString()).doesNotContain(SECRET);
    }

    @Test
    void userUpdateRequest_toString_doesNotContainPassword() {
        UserUpdateRequest req = new UserUpdateRequest();
        req.setUsername("alice");
        req.setEmail("alice@example.com");
        req.setPassword(SECRET);

        assertThat(req.toString()).doesNotContain(SECRET);
    }
}
