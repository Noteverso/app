package com.noteverso.core.security.service;

import com.noteverso.core.dao.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userMapper.findUserByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found with username " + username)
        );

        return UserDetailsImpl.build(user);
    }
}
