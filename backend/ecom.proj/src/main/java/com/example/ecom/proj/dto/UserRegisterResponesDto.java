package com.example.ecom.proj.dto;

import com.example.ecom.proj.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterResponesDto {
    private String firstname;
    private String lastname;
    private String email;
    private Role role;
    private AuthTokenDto token;
}
