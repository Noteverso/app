package com.noteverso.security.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.security.config.jwt.JwtUtils;
import com.noteverso.security.request.CreateUserRequest;
import com.noteverso.security.request.LoginRequest;
import com.noteverso.core.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Tag(name = "Auth", description = "Auth management APIs")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping("/login")
    public ApiResult<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);
        return ApiResult.success(token);
    }

    @PostMapping("/signup")
    public ApiResult<String> signup(@RequestBody @Valid CreateUserRequest createUserRequest) {
        userService.createUser(createUserRequest.getUsername(), createUserRequest.getPassword());
        return ApiResult.success("User created successfully");
    }

}
