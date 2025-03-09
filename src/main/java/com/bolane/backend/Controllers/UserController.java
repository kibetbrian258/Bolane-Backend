package com.bolane.backend.Controllers;

import com.bolane.backend.DTOs.JwtAuthenticationResponse;
import com.bolane.backend.DTOs.LoginRequestDTO;
import com.bolane.backend.DTOs.RefreshTokenRequest;
import com.bolane.backend.DTOs.UserRegistrationDTO;
import com.bolane.backend.Entities.User;
import com.bolane.backend.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(@Valid @RequestBody UserRegistrationDTO request) {
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.loginUser(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteUserProfile() {
        userService.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}