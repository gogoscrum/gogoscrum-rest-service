package com.shimi.gogoscrum.common.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HealthController is a controller that provides health check and CSRF token endpoints.
 */
@RestController
@Tag(name = "Health", description = "Health check")
@PermitAll
public class HealthController {
    @Operation(summary = "Check the health of the application")
    @GetMapping("/health")
    public Boolean checkHealth() {
        return true;
    }

    @Operation(summary = "Get the initial CSRF token for post actions such as login and register")
    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}
