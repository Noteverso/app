package com.noteverso.core.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String username;
    private String token;
}
