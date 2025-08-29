package com.shimi.gogoscrum.user.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.shimi.gogoscrum.common.util.IpUtil;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthenticationHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler, LogoutSuccessHandler {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationHandler.class);
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String CHARACTER = "UTF-8";
    private static final String MESSAGE_KEY = "message";

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        User principal = (User) authentication.getPrincipal();
        User user = userService.get(principal.getId());
        try {
            // To ensure the snowflake ID is serialized as a string
            ObjectMapper mapper = new ObjectMapper().registerModule(
                    new SimpleModule().addSerializer(Long.class, new ToStringSerializer()));
            Map<String, Object> map = new HashMap<>();
            map.put("user", user.toDto());
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(CHARACTER);
            response.getWriter().append(mapper.writeValueAsString(map));
        } catch (IllegalArgumentException e) {
            response.setCharacterEncoding(CHARACTER);
            Map<String, String> map = new HashMap<>();
            map.put("code", "badCredential");
            map.put("status", HttpServletResponse.SC_UNAUTHORIZED + ".1");
            map.put(MESSAGE_KEY, e.getMessage());
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(CHARACTER);
            response.getWriter().append(mapper.writeValueAsString(map));
            response.setStatus(200);
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        log.trace("Authentication Failed:", exception);
        response.setCharacterEncoding(CHARACTER);
        Map<String, String> map = new HashMap<>();
        map.put("code", "badCredential");
        map.put("status", HttpServletResponse.SC_UNAUTHORIZED + ".1");
        map.put(MESSAGE_KEY, exception.getMessage());
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER);
        response.getWriter().append(mapper.writeValueAsString(map));
        response.setStatus(200);
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<>();
        map.put(MESSAGE_KEY, "Logout successfully");
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER);
        response.getWriter().append(mapper.writeValueAsString(map));
        response.setStatus(200);
    }
}