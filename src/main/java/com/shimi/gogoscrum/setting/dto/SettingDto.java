package com.shimi.gogoscrum.setting.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.setting.model.Setting;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class SettingDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 7795750786963596146L;
    private Boolean allowSignup = Boolean.TRUE;

    @Override
    public Setting toEntity() {
        Setting entity = new Setting();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }

    public SettingDto() {
    }

    public SettingDto(Long id) {
        this.id = id;
    }

    public Boolean getAllowSignup() {
        return allowSignup;
    }

    public void setAllowSignup(Boolean allowSignup) {
        this.allowSignup = allowSignup;
    }
}

