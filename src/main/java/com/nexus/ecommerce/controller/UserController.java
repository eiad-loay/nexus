package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.AddressDto;
import com.nexus.ecommerce.dto.entity.UserDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.entity.User;
import com.nexus.ecommerce.service.AddressService;
import com.nexus.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AddressService addressService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Response<UserProfileResponse> getCurrentUser() {
        User user = userService.getActiveUser();
        log.info("GET /api/users/me - Fetching profile for user: {}", user.getEmail());

        UserDto userDto = userService.mapToDto(user);
        List<AddressDto> addresses = addressService.getAddressesForActiveUser();

        UserProfileResponse profile = new UserProfileResponse(userDto, addresses);
        log.debug("Profile retrieved for user: {}", user.getEmail());

        return Response.<UserProfileResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User profile retrieved successfully")
                .data(profile)
                .build();
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Response<UserDto> updateCurrentUser(@RequestBody @Valid UserDto userDto) {
        log.info("PUT /api/users/me - Updating user profile");
        UserDto updatedUser = userService.updateActiveUser(userDto);
        log.info("User profile updated successfully for: {}", updatedUser.getEmail());

        return Response.<UserDto>builder()
                .status(HttpStatus.OK.value())
                .message("User profile updated successfully")
                .data(updatedUser)
                .build();
    }

    /**
     * Response record for user profile containing user data and addresses
     */
    public record UserProfileResponse(UserDto user, List<AddressDto> addresses) {
    }
}
