package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.UserRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserResponse;
import de.dhbwravensburg.webeng.stagefinder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users")
    public List<UserResponse> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserResponse getById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "Username already taken")
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user account (owner only)")
    @ApiResponse(responseCode = "403", description = "Not the account owner")
    @ApiResponse(responseCode = "404", description = "User not found")
    public UserResponse update(@PathVariable Long id,
                               @Valid @RequestBody UserRequest request,
                               Authentication authentication) {
        return userService.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a user account (owner only)")
    @ApiResponse(responseCode = "204", description = "Deleted")
    @ApiResponse(responseCode = "403", description = "Not the account owner")
    @ApiResponse(responseCode = "404", description = "User not found")
    public void delete(@PathVariable Long id, Authentication authentication) {
        userService.delete(id, authentication.getName());
    }
}
