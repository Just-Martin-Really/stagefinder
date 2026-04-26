package de.dhbwravensburg.webeng.stagefinder.api.controller;

import de.dhbwravensburg.webeng.stagefinder.api.dto.AuthRequest;
import de.dhbwravensburg.webeng.stagefinder.api.dto.UserResponse;
import de.dhbwravensburg.webeng.stagefinder.domain.repository.UserRepository;
import de.dhbwravensburg.webeng.stagefinder.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Session-based authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Log in and start a session", description = "On success the server sets a JSESSIONID cookie.")
    @ApiResponse(responseCode = "200", description = "Authenticated")
    @ApiResponse(responseCode = "401", description = "Bad credentials")
    public UserResponse login(@Valid @RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        Authentication auth = authenticationManager.authenticate(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        return userRepository.findByUsername(auth.getName())
                .map(userService::toResponse)
                .orElseThrow();
    }

    @GetMapping("/me")
    @Operation(summary = "Return the currently authenticated user")
    @ApiResponse(responseCode = "401", description = "Not authenticated")
    public UserResponse me(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .map(userService::toResponse)
                .orElseThrow();
    }
}
