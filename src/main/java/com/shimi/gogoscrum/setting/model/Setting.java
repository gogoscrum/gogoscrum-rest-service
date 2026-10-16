package com.shimi.gogoscrum.setting.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.setting.dto.SettingDto;
import jakarta.persistence.Entity;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

@Entity
public class Setting extends BaseEntity {
    @Serial
    private static final long serialVersionUID = 4898400562299564187L;
    private Boolean allowSignup = Boolean.TRUE;

    @Override
    public SettingDto toDto() {
        return this.toDto(false);
    }

    @Override
    public SettingDto toDto(boolean detailed) {
        SettingDto dto = new SettingDto();
        BeanUtils.copyProperties(this, dto);

        if (detailed) {
            if (this.createdBy != null) {
                dto.setCreatedBy(this.createdBy.toDto());
            }

            if (this.updatedBy != null) {
                dto.setUpdatedBy(this.updatedBy.toDto());
            }
        }

        return dto;
    }

    public Setting() {
    }

    public Setting(Long id) {
        this.id = id;
    }

    public Boolean getAllowSignup() {
        return allowSignup;
    }

    public void setAllowSignup(Boolean allowSignup) {
        this.allowSignup = allowSignup;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Setting {");
        sb.append("id=").append(id);
        sb.append(", allowSignup=").append(allowSignup);
        sb.append('}');
        return sb.toString();
    }
}



