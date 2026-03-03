package com.noteverso.core.model.response;

import lombok.Data;

@Data
public class LoginResponse {
    private String email;
    private String token;
}
