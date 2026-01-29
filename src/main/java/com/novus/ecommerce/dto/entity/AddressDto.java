package com.novus.ecommerce.dto.entity;

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
public class AddressDto {

    private Long id;

    @NotEmpty(message = "Street cannot be empty")
    private String street;

    @NotEmpty(message = "City cannot be empty")
    @Size(min = 2, message = "City must be at least 2 characters")
    private String city;

    private String state;

    @NotEmpty(message = "Postal code cannot be empty")
    private String postalCode;

    @NotEmpty(message = "Country cannot be empty")
    private String country;

    private boolean isDefault;

    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}
