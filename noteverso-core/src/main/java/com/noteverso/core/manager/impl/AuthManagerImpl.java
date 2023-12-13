package com.noteverso.core.manager.impl;

import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;

public class AuthManagerImpl implements AuthManager {

    @Override
    public UserDetailsImpl getPrincipal(Authentication authentication) {
        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
