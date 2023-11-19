package com.noteverso.security.config.jwt;

import com.noteverso.common.context.TenantContext;
import com.noteverso.security.service.impl.UserDetailsImpl;
import com.noteverso.security.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse, @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(httpServletRequest);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);
                UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(username);
                TenantContext.setTenantId(userDetails.user().getUserId());
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch(Exception e) {
            log.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}
