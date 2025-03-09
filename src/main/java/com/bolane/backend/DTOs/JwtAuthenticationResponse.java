package com.bolane.backend.DTOs;

import com.bolane.backend.Entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthenticationResponse {
    private User user;
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
}
