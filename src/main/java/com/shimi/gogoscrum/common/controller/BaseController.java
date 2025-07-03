package com.shimi.gogoscrum.common.controller;

import com.shimi.gogoscrum.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base class for all controllers in the application.
 */
public abstract class BaseController {
    /**
     * Get the current authenticated user.
     * @return the current authenticated user, or null if no user is authenticated.
     */
    protected User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        } else {
            return null;
        }
    }
}
