package com.example.ecom.proj.dto;

import com.example.ecom.proj.enums.Role;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequestDto {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}
