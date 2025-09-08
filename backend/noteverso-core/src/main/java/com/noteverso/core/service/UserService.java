package com.noteverso.core.service;

public interface UserService {
    void createUser(String email, String username, String password);
    boolean existsByEmail(String email);
}
