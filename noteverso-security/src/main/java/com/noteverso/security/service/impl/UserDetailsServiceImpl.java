package com.noteverso.security.service.impl;

import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    public UserDetailsServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found with username " + username)
        );

        return new UserDetailsImpl(user);
    }
}
