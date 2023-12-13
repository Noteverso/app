package com.noteverso.core.manager;

import com.noteverso.core.security.service.UserDetailsImpl;
import org.springframework.security.core.Authentication;

public interface AuthManager {
    UserDetailsImpl getPrincipal(Authentication authentication);
}
