package com.bolane.backend.Services;

import com.bolane.backend.DTOs.JwtAuthenticationResponse;
import com.bolane.backend.DTOs.LoginRequestDTO;
import com.bolane.backend.DTOs.RefreshTokenRequest;
import com.bolane.backend.DTOs.UserRegistrationDTO;
import com.bolane.backend.Entities.User;
import com.bolane.backend.Exceptions.EmailAlreadyExistsException;
import com.bolane.backend.Exceptions.ResourceNotFoundException;
import com.bolane.backend.Exceptions.UnauthorizedException;
import com.bolane.backend.Repositories.UserRepository;
import com.bolane.backend.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtAuthenticationResponse registerUser(UserRegistrationDTO request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email address already in use!");
        }

        // Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER"); // Default role

        // Save user
        user = userRepository.save(user);

        // Generate tokens
        String accessToken = jwtTokenUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
        String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());

        // Return authentication response
        return JwtAuthenticationResponse.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtAuthenticationResponse loginUser(LoginRequestDTO request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

            // Generate tokens
            String accessToken = jwtTokenUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());

            // Return authentication response
            return JwtAuthenticationResponse.builder()
                    .user(user)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        try {
            // Extract username from refresh token
            String email = jwtTokenUtil.extractUsername(request.getRefreshToken());

            // Get user details
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // Generate new tokens
            String accessToken = jwtTokenUtil.generateToken(user.getEmail(), user.getRole(), user.getId());
            String refreshToken = jwtTokenUtil.generateRefreshToken(user.getEmail());

            // Return authentication response
            return JwtAuthenticationResponse.builder()
                    .user(user)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Refresh token has expired. Please login again");
        }
    }

    public User getUser(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public User getCurrentUser() {
        // Get authentication from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get username (email) from authentication
        String email = authentication.getName();

        // Return user details
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    @Transactional
    public void deleteCurrentUser() {
        User user = getCurrentUser();
        userRepository.delete(user);
    }
}