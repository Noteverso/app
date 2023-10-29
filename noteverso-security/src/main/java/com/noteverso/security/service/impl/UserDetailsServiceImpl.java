package com.noteverso.security.service.impl;

import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.user.dao.UserMapper;
import com.noteverso.user.model.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findUserByUsername(username).orElseThrow(
            () -> new NoSuchDataException("User not found with username " + username)
        );

        return new UserDetailsImpl(user);
    }
}
