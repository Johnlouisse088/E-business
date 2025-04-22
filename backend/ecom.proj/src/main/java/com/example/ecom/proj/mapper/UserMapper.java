package com.example.ecom.proj.mapper;

import com.example.ecom.proj.dto.AuthTokenDto;
import com.example.ecom.proj.dto.UserRegisterRequestDto;
import com.example.ecom.proj.dto.UserRegisterResponesDto;
import com.example.ecom.proj.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
// To transform between Entity and DTO, and vice versa.
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    // Get the 'registerRequest' then store it to the 'User' entity   : DTO -> Entity
    public User toEntity (UserRegisterRequestDto registerRequest) {
        return User.builder()
            .email(registerRequest.getEmail())
            .firstname(registerRequest.getFirstname())
            .lastname(registerRequest.getLastname())
            .password(passwordEncoder.encode(registerRequest.getPassword()))
            .role(registerRequest.getRole())
            .build();
    }

    // Get the 'user' from the database. and then store it in 'UserRegisterResponesDto'     : Entity -> DTO
    public UserRegisterResponesDto toDto (User savedUser, String accessToken, String refreshToken) {
        return UserRegisterResponesDto.builder()
            .email(savedUser.getEmail())
            .firstname(savedUser.getFirstname())
            .lastname(savedUser.getLastname())
            .role(savedUser.getRole())
            .token(AuthTokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build())
            .build();
    }
}
