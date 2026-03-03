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
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userMapper.findUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email " + email);
        }

        return UserDetailsImpl.build(user);
    }
}
