package com.shimi.gogoscrum.setting.controller;

import com.shimi.gogoscrum.common.controller.BaseController;
import com.shimi.gogoscrum.setting.dto.SettingDto;
import com.shimi.gogoscrum.setting.model.Setting;
import com.shimi.gogoscrum.setting.service.SettingService;
import com.shimi.gogoscrum.user.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/settings")
@CrossOrigin
@Tag(name = "Setting", description = "System setting management")
@RolesAllowed({User.ROLE_ADMIN})
public class SettingController extends BaseController {
    @Autowired
    private SettingService settingService;

    @Operation(summary = "Get current system setting")
    @GetMapping
    @RolesAllowed({User.ROLE_ADMIN})
    public SettingDto getCurrent() {
        return settingService.getCurrent().toDto(true);
    }

    @Operation(summary = "Check whether self-service signup is allowed")
    @GetMapping("/signup")
    @PermitAll
    public Boolean isSignupAllowed() {
        return settingService.isSignupAllowed();
    }

    @Operation(summary = "Update current system setting")
    @PutMapping
    @RolesAllowed({User.ROLE_ADMIN})
    public SettingDto updateCurrent(@RequestBody SettingDto settingDto) {
        Setting savedSetting = settingService.updateCurrent(settingDto.toEntity());
        return savedSetting.toDto(true);
    }
}






