package com.novus.ecommerce.dto.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    @NotEmpty(message = "username cannot be empty")
    @Size(min = 2)
    private String username;

    @Email(message = "please enter a valid email")
    private String email;

    private String role;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}