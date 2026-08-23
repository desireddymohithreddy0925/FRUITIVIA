package com.fruitivia.user.dto;

import com.fruitivia.user.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private boolean isEnabled;
    private LocalDateTime createdAt;
}
