package com.fruitivia.auth;

import com.fruitivia.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
}
