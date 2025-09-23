package com.shimi.gogoscrum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shimi.gogoscrum.user.authentication.AuthenticationHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.csrf.*;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Configuration
@EnableWebSecurity
@EnableScheduling
public class WebSecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private AuthenticationHandler logoutSuccessHandler;
    @Autowired
    private UserDetailsService userService;
    @Value("${security.config.rememberMeKey}")
    private String rememberMeKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] csrfIgnores = {"/files/upload"};
        String[] publicApis = {"/swagger-ui/**", "/v3/api-docs/**", "/health", "/csrf", "/users/register", "/users/login",
                "/users/oauth/**", "/docs/view/**"};
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                ).csrf(csrf -> csrf
                        .ignoringRequestMatchers(csrfIgnores)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
                ).authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicApis).permitAll().anyRequest().authenticated()
                ).formLogin(formLogin -> formLogin
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler)
                ).logout(logout -> logout
                        .logoutSuccessHandler(logoutSuccessHandler)
                ).exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(getAccessDeniedHandler())
                ).rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(rememberMeServices())
                );
        return http.build();
    }

    @Bean
    public AccessDeniedHandler getAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            log.trace("Resource AccessDeniedException: ", accessDeniedException);

            Map<String, String> map = new HashMap<>();

            map.put("message", accessDeniedException.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().append(mapper.writeValueAsString(map));
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        };
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            log.trace("Resource AccessDeniedException: ", authException);

            Map<String, String> map = new HashMap<>();
            map.put("message", authException.getMessage());

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().append(mapper.writeValueAsString(map));
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices tokenBasedRememberMeServices = new TokenBasedRememberMeServices(rememberMeKey, userService);
        tokenBasedRememberMeServices.setParameter("rememberMe");
        tokenBasedRememberMeServices.setTokenValiditySeconds(60 * 60 * 24 * 10);
        return tokenBasedRememberMeServices;
    }
}

// This customization is needed to provide CSRF protection for single-page applications
// https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa

/**
 * A {@link CsrfTokenRequestHandler} that handles CSRF tokens for single-page applications (SPA).
 * This customized handler is necessary according to the Spring Security documentation.
 * Refer to the documentation for more details:
 * <a href="https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa">
 *     https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa</a>
 */
final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        /*
         * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
         * the CsrfToken when it is rendered in the response body.
         */
        this.xor.handle(request, response, csrfToken);
        /*
         * Render the token value to a cookie by causing the deferred token to be loaded.
         */
        csrfToken.get();
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        String headerValue = request.getHeader(csrfToken.getHeaderName());
        /*
         * If the request contains a request header, use CsrfTokenRequestAttributeHandler
         * to resolve the CsrfToken. This applies when a single-page application includes
         * the header value automatically, which was obtained via a cookie containing the
         * raw CsrfToken.
         *
         * In all other cases (e.g. if the request contains a request parameter), use
         * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
         * when a server-side rendered form includes the _csrf request parameter as a
         * hidden input.
         */
        return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
    }
}