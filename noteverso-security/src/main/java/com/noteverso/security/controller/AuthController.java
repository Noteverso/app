package com.noteverso.security.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.security.request.CreateUserRequest;
import com.noteverso.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Auth", description = "Auth management APIs")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final IUserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/signup")
    public ApiResult<String> signup(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        return ApiResult.success("User created successfully");
    }

}
