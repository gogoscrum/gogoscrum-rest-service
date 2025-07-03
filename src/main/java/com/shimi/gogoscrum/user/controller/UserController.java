package com.shimi.gogoscrum.user.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.common.util.IpUtil;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.user.dto.UserDto;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import com.shimi.gogoscrum.user.model.UserFilter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin
@Tag(name = "User", description = "User management")
@RolesAllowed({User.ROLE_USER})
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Create a new user")
    @PostMapping("/register")
    @PermitAll
    public UserDto register(@RequestBody UserDto userDto, HttpServletRequest request) {
        userDto.setLastLoginIp(IpUtil.getIpAddr(request));
        User user = userDto.toEntity();
        return userService.create(user).toDto();
    }

    @Operation(summary = "Search Users")
    @Parameters({
            @Parameter(name = "filter", description = "The search filer")})
    @GetMapping
    public DtoQueryResult<Dto> search(UserFilter filter) {
        filter.setEnabled(Boolean.TRUE);
        EntityQueryResult<User> queryResult = userService.search(filter);
        return queryResult.toDto();
    }

    @Operation(summary = "Get a user")
    @Parameters({@Parameter(name = "id", description = "The ID of the user")})
    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        User user = userService.get(id);
        return user.toDto();
    }

    @Operation(summary = "Update current user's basic info")
    @Parameters({@Parameter(name = "id", description = "The ID of the user")})
    @PutMapping("/my/basics")
    public UserDto updateBasics( @RequestBody UserDto userDto) {
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

    @Operation(summary = "Check current user's password")
    @Parameters({@Parameter(name = "password", description = "The password of the user.")})
    @GetMapping("/my/pwd/check")
    public boolean checkOldPassword(@RequestParam(value = "password") String oldPassword) {
        return userService.checkPassword(this.getCurrentUser().getId(), oldPassword);
    }

    @Operation(summary = "Update current user's password")
    @PutMapping("/my/pwd")
    public void updatePassword(@RequestBody Map<String, String> params) {
        User user = (User) getCurrentUser();

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        userService.updatePassword(user.getId(), oldPassword, newPassword);
    }
}