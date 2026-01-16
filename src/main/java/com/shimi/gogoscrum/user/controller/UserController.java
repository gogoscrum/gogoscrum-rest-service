package com.shimi.gogoscrum.user.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.common.util.IpUtil;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.user.dto.UserDto;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserFilter;
import com.shimi.gogoscrum.user.oauth.OauthProvider;
import com.shimi.gogoscrum.user.service.UserService;
import com.shimi.gsf.core.dto.Dto;
import com.shimi.gsf.core.dto.DtoQueryResult;
import com.shimi.gsf.core.model.EntityQueryResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin
@Tag(name = "User", description = "User management")
@RolesAllowed({User.ROLE_USER})
public class UserController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private RememberMeServices rememberMeServices;

    @Operation(summary = "Create a new user")
    @PostMapping("/register")
    @PermitAll
    public UserDto register(@RequestBody UserDto userDto, HttpServletRequest request) {
        userDto.setLastLoginIp(IpUtil.getIpAddr(request));
        User user = userDto.toEntity();
        return userService.create(user).toDto();
    }

    /**
     * Search users with the given filter. If the platform is running in SaaS mode, when inviting users to join a project,
     * you should not use this API. Instead, use the "findProjectMates" API to search users who have joined at least one
     * common project with the current user. This is to avoid exposing all users in the system to each other.
     */
    @Operation(summary = "Search Users")
    @Parameters({
            @Parameter(name = "filter", description = "The search filer")})
    @GetMapping
    public DtoQueryResult<Dto> search(UserFilter filter) {
        filter.setEnabled(Boolean.TRUE);
        EntityQueryResult<User> queryResult = userService.search(filter);
        return queryResult.toDto();
    }

    /**
     * Search project mates, i.e., users who have joined at least one common project with the current user.
     * This is useful when inviting users to join a project while the platform is running as a SaaS service.
     * For those companies using self-hosted deployment, they can directly search all users.
     */
    @Operation(summary = "Search project mates, i.e., users who have joined at least one common project with the current user")
    @Parameters({
            @Parameter(name = "key", description = "The query keywords, can be username or nickname"),
            @Parameter(name = "page", description = "The page number, starting from 1"),
            @Parameter(name = "pageSize", description = "The page size")})
    @GetMapping("/mates")
    public DtoQueryResult<Dto> findProjectMates(UserFilter filter) {
        EntityQueryResult<User> queryResults = userService.findProjectMates(filter.getPage(), filter.getPageSize(), filter.getKeyword());
        return queryResults.toDto();
    }

    @Operation(summary = "Get a user")
    @Parameters({@Parameter(name = "id", description = "The ID of the user")})
    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        User user = userService.get(id);
        return user.toDto(true);
    }

    @Operation(summary = "Update current user's basic info")
    @Parameters({@Parameter(name = "id", description = "The ID of the user")})
    @PutMapping("/my/basics")
    public UserDto updateBasics(@RequestBody UserDto userDto) {
        User user = userDto.toEntity();
        User savedUser = userService.updateUserBasics(getCurrentUser().getId(), user);
        return savedUser.toDto();
    }

    @Operation(summary = "Update current user's avatar")
    @Parameters({@Parameter(name = "fileDto", description = "Avatar image file")})
    @PutMapping("/my/avatar")
    public UserDto updateAvatar(@RequestBody FileDto fileDto) {
        User user = userService.updateAvatar(getCurrentUser().getId(), fileDto.toEntity());
        return user.toDto();
    }

    @Operation(summary = "Delete current user's avatar")
    @DeleteMapping("/my/avatar")
    public void deleteAvatar() {
        userService.deleteAvatar(getCurrentUser().getId());
    }

    @Operation(summary = "Update current user's preference")
    @PutMapping("/my/preference")
    public UserDto updatePreference(@RequestBody Preference preference) {
        User savedUser = userService.updatePreference(getCurrentUser().getId(), preference);
        return savedUser.toDto();
    }

    @Operation(summary = "Update current user's password")
    @PutMapping("/my/pwd")
    public void updatePassword(@RequestBody Map<String, String> params) {
        User user = (User) getCurrentUser();

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        userService.updatePassword(user.getId(), oldPassword, newPassword);
    }

    @Operation(summary = "Get the list of configured 3rd-party OAuth providers")
    @GetMapping("/oauth/providers")
    @PermitAll
    public List<OauthProvider.ProviderConfig> getOauthProviders() {
        return userService.getOauthProviders();
    }

    @Operation(summary = "Get the login URL for a specific OAuth provider")
    @Parameters({@Parameter(name = "provider", description = "The unique name of the OAuth provider")})
    @GetMapping("/oauth/{provider}/login/url")
    @PermitAll
    public String getOauthLoginUrl(@PathVariable String provider) {
        return userService.getOauthProvider(provider).getLoginUrl();
    }

    @Operation(summary = "Login or register with 3rd party OAuth info")
    @PostMapping("/oauth/login")
    @PermitAll
    public UserDto loginByOauth(@RequestBody OauthProvider.OauthInfo oauthDto,
                                @RequestParam(value = "rememberMe") boolean rememberMe,
                                HttpServletRequest request, HttpServletResponse response) {
        User user = userService.retrieveUser(oauthDto);
        user.setLastLoginIp(IpUtil.getIpAddr(request));

        // If it's an existing user, login directly; if not, the UI will ask the user
        // whether to bind to an existing account or create a brand-new account.
        // Since this is a front-end logic, so put it in controller.
        if (user.getId() != null) {
            Authentication auth = this.authenticateUser(user, request, response);

            if (rememberMe) {
                rememberMeServices.loginSuccess(request, response, auth);
            }

            user = (User) auth.getPrincipal();
        }

        return user.toDto(true);
    }

    @Operation(summary = "Create a new user or bind to an existing user")
    @PostMapping("/oauth/register")
    @PermitAll
    public UserDto createOrBindFromOauth(@RequestBody UserDto userDto,
                                         @RequestParam(value = "rememberMe") boolean rememberMe,
                                         HttpServletRequest request, HttpServletResponse response) {
        User user = userDto.toEntity();
        user.setLastLoginIp(IpUtil.getIpAddr(request));
        // Set avatar if provided
        if (StringUtils.hasText(userDto.getAvatarUrl())) {
            File avatarFile = new File();
            avatarFile.setFullPath(userDto.getAvatarUrl());
            user.setAvatar(avatarFile);
        }
        User createdUser = userService.createOrBindFromOauth(user);

        if (createdUser.getId() != null) {
            Authentication auth = this.authenticateUser(createdUser, request, response);

            if (rememberMe) {
                rememberMeServices.loginSuccess(request, response, auth);
            }
        }

        return createdUser.toDto(true);
    }

    @Operation(summary = "Unbind 3rd-party OAuth from current user")
    @Parameters({@Parameter(name = "bindingId", description = "The ID of the UserBinding to unbind")})
    @DeleteMapping("/oauth/bindings/{bindingId}")
    public void unbindOauth(@PathVariable Long bindingId) {
        userService.unbindOauth(bindingId);
    }

    /**
     * Manually authenticate a user and set the authentication in the security context.
     */
    private Authentication authenticateUser(User user, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        // Create a fresh SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // Explicitly persist the SecurityContext into session (Spring Boot 3.x required)
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
        log.debug("User logged in: {}", auth.getPrincipal());

        return auth;
    }
}