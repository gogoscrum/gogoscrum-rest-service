package com.shimi.gogoscrum.common.dto;

import com.shimi.gogoscrum.user.dto.UserDto;
import com.shimi.gsf.core.dto.TraceableDto;

/**
 * Base class for all DTOs (Data Transfer Object) in the application.
 * It extends the TraceableDto class and includes fields for the user who created and updated the DTO.
 */
@SuppressWarnings("serial")
public abstract class BaseDto extends TraceableDto {
    protected UserDto createdBy;
    protected UserDto updatedBy;

    @Override
    public UserDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserDto createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public UserDto getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UserDto updatedBy) {
        this.updatedBy = updatedBy;
    }
}
