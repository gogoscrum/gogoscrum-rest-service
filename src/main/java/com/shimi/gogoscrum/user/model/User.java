package com.shimi.gogoscrum.user.model;

import com.shimi.gogoscrum.common.model.BaseEntity;
import com.shimi.gogoscrum.file.dto.FileDto;
import com.shimi.gogoscrum.file.model.File;
import com.shimi.gogoscrum.user.dto.UserDto;
import com.shimi.gogoscrum.user.utils.UserPreferenceConverter;
import jakarta.persistence.*;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.util.*;

@Entity
public class User extends BaseEntity implements com.shimi.gsf.core.model.User {
    @Serial
    private static final long serialVersionUID = -2322205969864150058L;

    public static final String ROLE_USER = "ROLE_USER";

    private String username;
    private String nickname;
    private String password;
    private String phone;
    private String email;
    @OneToOne
    @JoinColumn(name = "avatar_file_id")
    private File avatar;
    private boolean enabled = true;
    private Date lastLoginTime;
    private String lastLoginIp;
    @Convert(converter = UserPreferenceConverter.class)
    private Preference preference;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private List<UserBinding> bindings = new ArrayList<>();

    @Transient
    private boolean bindToExistingUser;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    @Override
    public UserDto toDto() {
        return this.toDto(false);
    }

    @Override
    public UserDto toDto(boolean detailed) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(this, dto, "password", "password", "bindings");

        if (this.avatar != null) {
            // to avoid circular reference, we only set the URL in the DTO
            FileDto avatarDto = new FileDto();
            avatarDto.setUrl(avatar.getUrl());
            dto.setAvatar(avatarDto);
        }

        if (detailed) {
            if (!CollectionUtils.isEmpty(this.bindings)) {
                dto.setBindings(this.bindings.stream().map(UserBinding::toDto).toList());
            }

            dto.setHasPassword(StringUtils.hasText(this.password));
        }

        return dto;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Get the authorities granted to the user. We only have one role for now.
     * @return a collection of granted authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return authorities;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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

    public File getAvatar() {
        return avatar;
    }

    public void setAvatar(File avatar) {
        this.avatar = avatar;
    }

    public List<UserBinding> getBindings() {
        return bindings;
    }

    public void setBindings(List<UserBinding> bindings) {
        this.bindings = bindings;
    }

    public boolean isBindToExistingUser() {
        return bindToExistingUser;
    }

    public void setBindToExistingUser(boolean bindToExistingUser) {
        this.bindToExistingUser = bindToExistingUser;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}
