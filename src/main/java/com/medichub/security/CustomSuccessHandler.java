package com.medichub.security;


import com.medichub.controller.admin.AdminController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(CustomSuccessHandler.class);


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Forwarding depending on the role:
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        log.info("Authentication successful for user: " + authentication.getName());
        log.info("Roles: " + roles);

        if (roles.contains("ROLE_ADMIN")) {
            log.info("Redirect to /admin/dashboard");
            response.sendRedirect("/admin/dashboard");
        } else if (roles.contains("ROLE_USER")) {
            log.info("Redirect to /auth/account");
            response.sendRedirect("/auth/account");
        } else {
            log.warn("Unknown role, redirect to login");
            response.sendRedirect("/auth/login?error=role");
        }
    }
}
