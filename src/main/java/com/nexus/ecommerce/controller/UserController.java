package com.nexus.ecommerce.controller;

import com.nexus.ecommerce.dto.entity.UserDto;
import com.nexus.ecommerce.dto.response.Response;
import com.nexus.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
}
