package com.noteverso.user.service.impl;

import com.noteverso.user.dao.UserMapper;
import com.noteverso.user.model.User;
import com.noteverso.user.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(String username, String password) {
        var user = User
            .builder()
            .email(username)
            .username(username)
            .password(passwordEncoder.encode(password))
            .hasPassword(true)
            .joinedAt(Instant.now())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        userMapper.insert(user);
    }
}
