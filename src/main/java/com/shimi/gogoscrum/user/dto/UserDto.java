package com.shimi.gogoscrum.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.shimi.gogoscrum.common.dto.BaseDto;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.user.model.Preference;
import com.shimi.gogoscrum.user.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto extends BaseDto implements com.shimi.gsf.core.dto.UserDto {
    @Serial
    private static final long serialVersionUID = -5842231722816716788L;

    private String nickname;
    private String username;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    private String phone;
    private String email;
    private FileDto avatar;
    private boolean enabled = true;
    private Date lastLoginTime;
    private String lastLoginIp;
    private Preference preference;
    private List<UserBindingDto> bindings = new ArrayList<>();
    private boolean bindToExistingUser;

    @Override
    public User toEntity() {
        User entity = new User();
        BeanUtils.copyProperties(this, entity, "bindings");
        if (!CollectionUtils.isEmpty(bindings)) {
            entity.setBindings(bindings.stream().map(UserBindingDto::toEntity).collect(Collectors.toList()));
        }
        return entity;
    }

    public UserDto() {
    }

    public UserDto(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getAvatarUrl() {
        return avatar != null ? avatar.getUrl() : null;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Preference getPreference() {
        return preference;
    }

    public void setPreference(Preference preference) {
        this.preference = preference;
    }

    public FileDto getAvatar() {
        return avatar;
    }

    public void setAvatar(FileDto avatar) {
        this.avatar = avatar;
    }

    public List<UserBindingDto> getBindings() {
        return bindings;
    }

    public void setBindings(List<UserBindingDto> bindings) {
        this.bindings = bindings;
    }

    public boolean isBindToExistingUser() {
        return bindToExistingUser;
    }

    public void setBindToExistingUser(boolean bindToExistingUser) {
        this.bindToExistingUser = bindToExistingUser;
    }

    /**
     * Normalize the DTO to remove any sensitive information.
     * @return the normalized User DTO
     */
    @Override
    public UserDto normalize() {
        UserDto normalized = new UserDto();

        normalized.setId(this.id);
        normalized.setNickname(this.nickname);
        normalized.setUsername(this.username);
        normalized.setAvatar(this.avatar);

        return normalized;
    }
}
