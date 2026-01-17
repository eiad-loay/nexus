package com.nexus.ecommerce.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String username;
    private String email;
    private String message;
}
