package com.noteverso.core.service;

import com.noteverso.core.model.UserConfig;

public interface UserService {
    void createUser(String email, String username, String password);
    boolean existsByEmail(String email);
}
