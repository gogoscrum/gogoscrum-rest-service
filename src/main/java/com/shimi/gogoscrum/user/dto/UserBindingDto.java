package com.shimi.gogoscrum.user.dto;

import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.user.model.UserBinding;
import org.springframework.beans.BeanUtils;

import java.io.Serial;

public class UserBindingDto extends BaseDto {
    @Serial
    private static final long serialVersionUID = 1444198311203222768L;
    private String provider;
    private String extUserId;
    private String unionId;
    private UserDto user;

    @Override
    public UserBinding toEntity() {
        UserBinding bind = new UserBinding();
        BeanUtils.copyProperties(this, bind);
        return bind;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExtUserId() {
        return extUserId;
    }

    public void setExtUserId(String extUserId) {
        this.extUserId = extUserId;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
