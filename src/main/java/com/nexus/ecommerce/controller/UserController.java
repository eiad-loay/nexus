package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.UserDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Response<UserDto> getCurrentUser() {
        UserDto userDto = userService.getActiveUser();

        return Response.<UserDto>builder()
                        .status(HttpStatus.OK.value())
                        .data(userDto)
                        .message("success")
                        .build();
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public Response<UserDto> updateCurrentUser(@RequestBody UserDto userDto) {
        UserDto user = userService.updateActiveUser();

        return Response.<UserDto>builder()
                .status(HttpStatus.OK.value())
                .message("success")
                .data(user)
                .build();
    }

}
